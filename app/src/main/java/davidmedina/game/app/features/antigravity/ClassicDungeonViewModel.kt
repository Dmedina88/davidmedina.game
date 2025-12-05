package davidmedina.game.app.features.antigravity

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

// Grid-based dungeon system
data class GridPosition(val x: Int, val y: Int) {
    fun distanceTo(other: GridPosition): Int {
        return kotlin.math.abs(x - other.x) + kotlin.math.abs(y - other.y)
    }
}

enum class TileType {
    FLOOR,
    WALL,
    DOOR,
    STAIRS_DOWN,
    STAIRS_UP,
    HEALING_FOUNTAIN  // Restores HP when stepped on
}

data class DungeonTile(
    val position: GridPosition,
    val type: TileType,
    val isRevealed: Boolean = false,
    val isVisible: Boolean = false
)

enum class ItemType {
    WEAPON,
    ARMOR,
    POTION,
    SCROLL,
    KEY,
    GOLD
}

data class Item(
    val id: Int,
    val name: String,
    val type: ItemType,
    val emoji: String,
    val position: GridPosition? = null, // Where item is on the map
    val rarity: String = "Common", // Common, Rare, Epic, Legendary
    val value: Int = 0,
    val attack: Int = 0,
    val defense: Int = 0,
    val healing: Int = 0
)

data class DungeonEnemy(
    val id: Int,
    val name: String,
    val emoji: String,
    val position: GridPosition,
    val hp: Int,
    val maxHp: Int,
    val attack: Int,
    val defense: Int,
    val xpReward: Int,
    val isAlive: Boolean = true,
    val isAggressive: Boolean = false,
    val isBoss: Boolean = false
)

data class Player(
    val position: GridPosition = GridPosition(5, 5),
    val hp: Int = 100,
    val maxHp: Int = 100,
    val mana: Int = 50,
    val maxMana: Int = 50,
    val level: Int = 1,
    val xp: Int = 0,
    val xpToNextLevel: Int = 100,
    val attack: Int = 10,
    val defense: Int = 5,
    val gold: Int = 0,
    val inventory: List<Item> = emptyList(),
    val equippedWeapon: Item? = null,
    val equippedArmor: Item? = null
) {
    fun getTotalAttack(): Int = attack + (equippedWeapon?.attack ?: 0)
    fun getTotalDefense(): Int = defense + (equippedArmor?.defense ?: 0)
}

enum class DungeonGameState {
    EXPLORING,
    IN_COMBAT,
    INVENTORY,
    GAME_OVER,
    VICTORY,
    LEVEL_UP
}

data class ClassicDungeonState(
    val gameState: DungeonGameState = DungeonGameState.EXPLORING,
    val player: Player = Player(),
    val dungeonLevel: Int = 1,
    val tiles: List<DungeonTile> = emptyList(),
    val enemies: List<DungeonEnemy> = emptyList(),
    val items: List<Item> = emptyList(),
    val currentEnemy: DungeonEnemy? = null,
    val messages: List<String> = listOf("Welcome to Classic Dungeon!"),
    val turnCount: Int = 0,
    val gridWidth: Int = 20,
    val gridHeight: Int = 20,
    val viewportSize: Int = 11, // 11x11 viewport centered on player
    val isAutoPlaying: Boolean = false // AI auto-play mode
)

class ClassicDungeonViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(ClassicDungeonState())
    val state = _state.asStateFlow()
    
    private var nextItemId = 0
    private var nextEnemyId = 0
    private val autoPlayAgent = AutoPlayAgent()
    private var autoPlayJob: kotlinx.coroutines.Job? = null
    
    fun startNewGame() {
        nextItemId = 0
        nextEnemyId = 0
        
        val initialDungeon = generateDungeon(20, 20, 1)
        _state.update {
            ClassicDungeonState(
                gameState = DungeonGameState.EXPLORING,
                player = Player(position = initialDungeon.playerSpawn),
                dungeonLevel = 1,
                tiles = initialDungeon.tiles,
                enemies = initialDungeon.enemies,
                items = initialDungeon.items,
                messages = listOf("🗡️ Welcome to Classic Dungeon!", "Explore and survive!"),
                turnCount = 0
            )
        }
    }
    
    fun movePlayer(direction: GridPosition) {
        val currentState = _state.value
        if (currentState.gameState != DungeonGameState.EXPLORING) return
        
        val newPos = GridPosition(
            currentState.player.position.x + direction.x,
            currentState.player.position.y + direction.y
        )
        
        // Check if move is valid
        val tile = currentState.tiles.find { it.position == newPos }
        if (tile == null || tile.type == TileType.WALL) {
            addMessage("You can't move there!")
            return
        }
        
        // Check for enemy collision
        val enemy = currentState.enemies.find { it.position == newPos && it.isAlive }
        if (enemy != null) {
            enterCombat(enemy)
            return
        }
        
        // Check for item pickup at new position
        val itemAtPosition = currentState.items.find { it.position == newPos }
        
        // Move player
        _state.update { state ->
            var updatedState = state.copy(
                player = state.player.copy(position = newPos),
                turnCount = state.turnCount + 1
            )
            
            // Auto-pickup item if present
            if (itemAtPosition != null) {
                updatedState = when (itemAtPosition.type) {
                    ItemType.GOLD -> {
                        addMessage("💰 Found ${itemAtPosition.value} gold!")
                        updatedState.copy(
                            player = updatedState.player.copy(gold = updatedState.player.gold + itemAtPosition.value),
                            items = updatedState.items.filter { it.id != itemAtPosition.id }
                        )
                    }
                    ItemType.POTION -> {
                        val healAmount = itemAtPosition.healing.coerceAtMost(updatedState.player.maxHp - updatedState.player.hp)
                        addMessage("❤️ Used ${itemAtPosition.name}! Healed for $healAmount HP!")
                        updatedState.copy(
                            player = updatedState.player.copy(hp = (updatedState.player.hp + healAmount).coerceAtMost(updatedState.player.maxHp)),
                            items = updatedState.items.filter { it.id != itemAtPosition.id }
                        )
                    }
                    else -> {
                        addMessage("📦 Picked up ${itemAtPosition.emoji} ${itemAtPosition.name} [${itemAtPosition.rarity}]")
                        updatedState.copy(
                            player = updatedState.player.copy(inventory = updatedState.player.inventory + itemAtPosition),
                            items = updatedState.items.filter { it.id != itemAtPosition.id }
                        )
                    }
                }
            }
            
            // Reveal and update visibility
            updatedState = updatedState.copy(
                tiles = updateVisibility(updatedState.tiles, newPos)
            )
            
            updatedState
        }
        
        // Check for healing fountain
        if (tile.type == TileType.HEALING_FOUNTAIN) {
            val healAmount = 30
            _state.update { state ->
                val newHp = (state.player.hp + healAmount).coerceAtMost(state.player.maxHp)
                val actualHealed = newHp - state.player.hp
                
                if (actualHealed > 0) {
                    addMessage("⛲ The fountain heals you for $actualHealed HP!")
                } else {
                    addMessage("⛲ You're already at full health")
                }
                
                state.copy(
                    player = state.player.copy(hp = newHp)
                )
            }
        }
        
        // Check for stairs
        if (tile.type == TileType.STAIRS_DOWN) {
            descendStairs()
        }
        
        // Enemy turn
        viewModelScope.launch {
            delay(100)
            processEnemyTurns()
        }
    }
    
    fun pickupItem(itemId: Int) {
        val currentState = _state.value
        val item = currentState.items.find { it.id == itemId }
        
        if (item == null) return
        
        _state.update { state ->
            val updatedPlayer = when (item.type) {
                ItemType.GOLD -> {
                    addMessage("💰 Found ${item.value} gold!")
                    state.player.copy(gold = state.player.gold + item.value)
                }
                ItemType.POTION -> {
                    val healAmount = item.healing.coerceAtMost(state.player.maxHp - state.player.hp)
                    addMessage("❤️ Healed for $healAmount HP!")
                    state.player.copy(hp = (state.player.hp + healAmount).coerceAtMost(state.player.maxHp))
                }
                else -> {
                    addMessage("📦 Picked up ${item.emoji} ${item.name}")
                    state.player.copy(inventory = state.player.inventory + item)
                }
            }
            
            state.copy(
                player = updatedPlayer,
                items = state.items.filter { it.id != itemId }
            )
        }
    }
    
    fun attack() {
        val currentState = _state.value
        val enemy = currentState.currentEnemy ?: return
        
        if (currentState.gameState != DungeonGameState.IN_COMBAT) return
        
        // Player attacks
        val damage = calculateDamage(currentState.player.getTotalAttack(), enemy.defense)
        val newEnemyHp = (enemy.hp - damage).coerceAtLeast(0)
        
        _state.update { state ->
            val updatedEnemy = enemy.copy(hp = newEnemyHp, isAlive = newEnemyHp > 0)
            
            state.copy(
                currentEnemy = updatedEnemy,
                enemies = state.enemies.map { if (it.id == enemy.id) updatedEnemy else it }
            )
        }
        
        addMessage("⚔️ You hit ${enemy.name} for $damage damage!")
        
        if (newEnemyHp <= 0) {
            onEnemyDefeated(enemy)
        } else {
            // Enemy counter-attack
            viewModelScope.launch {
                delay(500)
                enemyAttack(enemy)
            }
        }
    }
    
    fun defend() {
        val currentState = _state.value
        val enemy = currentState.currentEnemy ?: return
        
        addMessage("🛡️ You brace for impact! (Defense +5 this turn)")
        
        // Temporary defense boost
        _state.update { state ->
            state.copy(
                player = state.player.copy(defense = state.player.defense + 5)
            )
        }
        
        viewModelScope.launch {
            delay(500)
            enemyAttack(enemy)
            
            // Remove defense boost
            _state.update { state ->
                state.copy(
                    player = state.player.copy(defense = state.player.defense - 5)
                )
            }
        }
    }
    
    fun useMagic() {
        val currentState = _state.value
        val enemy = currentState.currentEnemy ?: return
        val manaCost = 15
        
        if (currentState.player.mana < manaCost) {
            addMessage("❌ Not enough mana!")
            return
        }
        
        val damage = (currentState.player.getTotalAttack() * 1.5).toInt()
        val newEnemyHp = (enemy.hp - damage).coerceAtLeast(0)
        
        _state.update { state ->
            val updatedEnemy = enemy.copy(hp = newEnemyHp, isAlive = newEnemyHp > 0)
            
            state.copy(
                player = state.player.copy(mana = state.player.mana - manaCost),
                currentEnemy = updatedEnemy,
                enemies = state.enemies.map { if (it.id == enemy.id) updatedEnemy else it }
            )
        }
        
        addMessage("✨ Magic attack hits ${enemy.name} for $damage damage!")
        
        if (newEnemyHp <= 0) {
            onEnemyDefeated(enemy)
        } else {
            viewModelScope.launch {
                delay(500)
                enemyAttack(enemy)
            }
        }
    }
    
    fun flee() {
        val currentState = _state.value
        if (Random.nextFloat() < 0.7f) {
            addMessage("🏃 You successfully fled!")
            _state.update { it.copy(gameState = DungeonGameState.EXPLORING, currentEnemy = null) }
        } else {
            addMessage("❌ Couldn't escape!")
            val enemy = currentState.currentEnemy ?: return
            viewModelScope.launch {
                delay(500)
                enemyAttack(enemy)
            }
        }
    }
    
    fun equipItem(item: Item) {
        _state.update { state ->
            when (item.type) {
                ItemType.WEAPON -> {
                    val oldWeapon = state.player.equippedWeapon
                    var inventory = state.player.inventory.filter { it.id != item.id }
                    if (oldWeapon != null) {
                        inventory = inventory + oldWeapon
                    }
                    addMessage("⚔️ Equipped ${item.name}")
                    state.copy(
                        player = state.player.copy(
                            equippedWeapon = item,
                            inventory = inventory
                        )
                    )
                }
                ItemType.ARMOR -> {
                    val oldArmor = state.player.equippedArmor
                    var inventory = state.player.inventory.filter { it.id != item.id }
                    if (oldArmor != null) {
                        inventory = inventory + oldArmor
                    }
                    addMessage("🛡️ Equipped ${item.name}")
                    state.copy(
                        player = state.player.copy(
                            equippedArmor = item,
                            inventory = inventory
                        )
                    )
                }
                else -> state
            }
        }
    }
    
    fun toggleInventory() {
        _state.update {
            it.copy(
                gameState = if (it.gameState == DungeonGameState.INVENTORY) 
                    DungeonGameState.EXPLORING 
                else 
                    DungeonGameState.INVENTORY
            )
        }
    }
    
    // Private helper functions
    
    private fun enterCombat(enemy: DungeonEnemy) {
        _state.update {
            it.copy(
                gameState = DungeonGameState.IN_COMBAT,
                currentEnemy = enemy
            )
        }
        addMessage("⚠️ Combat with ${enemy.emoji} ${enemy.name}!")
    }
    
    private fun enemyAttack(enemy: DungeonEnemy) {
        val currentState = _state.value
        if (!enemy.isAlive) return
        
        val damage = calculateDamage(enemy.attack, currentState.player.getTotalDefense())
        val newHp = (currentState.player.hp - damage).coerceAtLeast(0)
        
        _state.update { state ->
            state.copy(player = state.player.copy(hp = newHp))
        }
        
        addMessage("💥 ${enemy.name} hits you for $damage damage!")
        
        if (newHp <= 0) {
            gameOver()
        }
    }
    
    private fun onEnemyDefeated(enemy: DungeonEnemy) {
        val xpGained = enemy.xpReward
        val goldDrop = Random.nextInt(5, 20)
        
        // Generate loot drop (60% chance, 100% for bosses)
        val dropChance = if (enemy.isBoss) 1.0f else 0.6f
        val droppedItem = if (Random.nextFloat() < dropChance) {
            generateLootDrop(enemy)
        } else null
        
        _state.update { state ->
            val newXp = state.player.xp + xpGained
            val newGold = state.player.gold + goldDrop
            
            var updatedPlayer = state.player.copy(xp = newXp, gold = newGold)
            var newGameState = DungeonGameState.EXPLORING
            
            // Check for level up
            if (newXp >= state.player.xpToNextLevel) {
                updatedPlayer = levelUp(updatedPlayer)
                newGameState = DungeonGameState.LEVEL_UP
            }
            
            // Add dropped item to world
            val updatedItems = if (droppedItem != null) {
                state.items + droppedItem
            } else state.items
            
            // Check if boss was defeated (victory!)
            val finalGameState = if (enemy.isBoss) {
                DungeonGameState.VICTORY
            } else newGameState
            
            state.copy(
                player = updatedPlayer,
                gameState = finalGameState,
                currentEnemy = null,
                enemies = state.enemies.map { if (it.id == enemy.id) it.copy(isAlive = false) else it },
                items = updatedItems
            )
        }
        
        addMessage("🎉 Defeated ${enemy.name}! +$xpGained XP, +$goldDrop gold")
        
        if (droppedItem != null) {
            addMessage("💎 ${droppedItem.emoji} ${droppedItem.name} dropped!")
        }
        
        if (enemy.isBoss) {
            addMessage("🏆 VICTORY! You defeated the final boss!")
        }
    }
    
    private fun levelUp(player: Player): Player {
        val newLevel = player.level + 1
        addMessage("⭐ LEVEL UP! You are now level $newLevel!")
        
        return player.copy(
            level = newLevel,
            xp = 0,
            xpToNextLevel = player.xpToNextLevel + 50,
            maxHp = player.maxHp + 20,
            hp = player.maxHp + 20,
            maxMana = player.maxMana + 10,
            mana = player.maxMana + 10,
            attack = player.attack + 3,
            defense = player.defense + 2
        )
    }
    
    fun continuePlaying() {
        _state.update { it.copy(gameState = DungeonGameState.EXPLORING) }
    }
    
    private fun calculateDamage(attack: Int, defense: Int): Int {
        val baseDamage = attack - (defense / 2)
        val variance = Random.nextInt(-2, 3)
        return (baseDamage + variance).coerceAtLeast(1)
    }
    
    private fun gameOver() {
        _state.update { it.copy(gameState = DungeonGameState.GAME_OVER) }
        addMessage("💀 You have been defeated!")
    }
    
    private fun descendStairs() {
        val newLevel = _state.value.dungeonLevel + 1
        val dungeon = generateDungeon(20, 20, newLevel)
        
        _state.update {
            it.copy(
                dungeonLevel = newLevel,
                tiles = dungeon.tiles,
                enemies = dungeon.enemies,
                items = dungeon.items,
                player = it.player.copy(position = dungeon.playerSpawn)
            )
        }
        
        addMessage("📥 Descended to dungeon level $newLevel")
    }
    
    private fun processEnemyTurns() {
        val currentState = _state.value
        if (currentState.gameState != DungeonGameState.EXPLORING) return
        
        currentState.enemies.filter { it.isAlive }.forEach { enemy ->
            // Simple AI: move towards player if close
            val distance = enemy.position.distanceTo(currentState.player.position)
            
            if (distance <= 5 && distance > 1) {
                moveEnemyTowardsPlayer(enemy)
            } else if (distance == 1) {
                enterCombat(enemy)
            }
        }
    }
    
    private fun moveEnemyTowardsPlayer(enemy: DungeonEnemy) {
        val currentState = _state.value
        val playerPos = currentState.player.position
        val enemyPos = enemy.position
        
        val dx = when {
            playerPos.x > enemyPos.x -> 1
            playerPos.x < enemyPos.x -> -1
            else -> 0
        }
        
        val dy = when {
            playerPos.y > enemyPos.y -> 1
            playerPos.y < enemyPos.y -> -1
            else -> 0
        }
        
        val newPos = GridPosition(enemyPos.x + dx, enemyPos.y + dy)
        
        // Check if move is valid
        val tile = currentState.tiles.find { it.position == newPos }
        if (tile != null && tile.type == TileType.FLOOR) {
            _state.update { state ->
                state.copy(
                    enemies = state.enemies.map { 
                        if (it.id == enemy.id) it.copy(position = newPos) else it 
                    }
                )
            }
        }
    }
    
    // Loot generation (template-based with variety)
    private fun generateLootDrop(enemy: DungeonEnemy): Item {
        val dungeonLevel = _state.value.dungeonLevel
        
        // Determine rarity (bosses drop better loot)
        val rarity = if (enemy.isBoss) {
            when (Random.nextFloat()) {
                in 0f..0.5f -> "Legendary"
                else -> "Epic"
            }
        } else {
            when (Random.nextFloat()) {
                in 0f..0.02f -> "Legendary"
                in 0.02f..0.10f -> "Epic"
                in 0.10f..0.30f -> "Rare"
                else -> "Common"
            }
        }
        
        // Item type (70% equipment, 30% consumable)
        val isEquipment = Random.nextFloat() < 0.7f
        
        return if (isEquipment) {
            generateEquipmentDrop(enemy, dungeonLevel, rarity)
        } else {
            generateConsumableDrop(enemy, dungeonLevel, rarity)
        }
    }
    
    private fun generateEquipmentDrop(enemy: DungeonEnemy, level: Int, rarity: String): Item {
        val isWeapon = Random.nextBoolean()
        
        // Item name templates
        val prefixes = listOf("Flame", "Frost", "Shadow", "Holy", "Cursed",
            "Ancient", "Blessed", "Demonic", "Dragon", "Phantom", "Storm", "Void")
        val suffixes = listOf("Slayer", "Bane", "Guard", "Strike", "Wrath",
            "Fury", "Doom", "Grace", "Might", "Edge", "Soul", "Fang")
        
        val weapons = listOf("Sword", "Axe", "Dagger", "Spear", "Mace", "Blade")
        val armors = listOf("Helm", "Plate", "Shield", "Gauntlets", "Boots", "Cloak")
        
        val prefix = prefixes.random()
        val suffix = suffixes.random()        
        val baseItem = if (isWeapon) weapons.random() else armors.random()
        
        val name = "$prefix $baseItem of $suffix"
        
        // Stats based on rarity and level
        val statMultiplier = when (rarity) {
            "Legendary" -> 2.0f
            "Epic" -> 1.5f
            "Rare" -> 1.2f
            else -> 1.0f
        }
        
        val attack = if (isWeapon) {
            ((5 + level * 2) * statMultiplier + Random.nextInt(-2, 3)).toInt()
        } else 0
        
        val defense = if (!isWeapon) {
            ((3 + level) * statMultiplier + Random.nextInt(-1, 2)).toInt()
        } else 0
        
        return Item(
            id = nextItemId++,
            name = name,
            type = if (isWeapon) ItemType.WEAPON else ItemType.ARMOR,
            emoji = if (isWeapon) "⚔️" else "🛡️",
            position = enemy.position,
            rarity = rarity,
            attack = attack,
            defense = defense
        )
    }
    
    private fun generateConsumableDrop(enemy: DungeonEnemy, level: Int, rarity: String): Item {
        val potionTypes = listOf(
            Triple("Health Potion", "❤️", ItemType.POTION) to 30,
            Triple("Greater Health Potion", "💖", ItemType.POTION) to 50,
            Triple("Mana Potion", "💙", ItemType.POTION) to 25,
            Triple("Elixir of Life", "✨", ItemType.POTION) to 100
        )
        
        val (itemData, healing) = if (rarity == "Legendary" || rarity == "Epic") {
            potionTypes[Random.nextInt(2, potionTypes.size)] // Better potions
        } else {
            potionTypes[Random.nextInt(0, 2)] // Basic potions
        }
        
        val (name, emoji, type) = itemData
        
        return Item(
            id = nextItemId++,
            name = name,
            type = type,
            emoji = emoji,
            position = enemy.position,
            rarity = rarity,
            healing = healing
        )
    }
    
    private fun updateVisibility(tiles: List<DungeonTile>, playerPos: GridPosition): List<DungeonTile> {
        val viewDistance = 5
        
        return tiles.map { tile ->
            val distance = kotlin.math.sqrt(
                ((tile.position.x - playerPos.x) * (tile.position.x - playerPos.x) +
                (tile.position.y - playerPos.y) * (tile.position.y - playerPos.y)).toDouble()
            )
            
            val isVisible = distance <= viewDistance
            
            tile.copy(
                isVisible = isVisible,
                isRevealed = tile.isRevealed || isVisible
            )
        }
    }
    
    private fun addMessage(message: String) {
        _state.update {
            it.copy(messages = (it.messages + message).takeLast(5))
        }
    }
    
    // Dungeon generation (basic procedural, will be ML-enhanced later)
    private fun generateDungeon(width: Int, height: Int, level: Int): GeneratedDungeon {
        val tiles = mutableListOf<DungeonTile>()
        
        // Initialize all as walls
        for (y in 0 until height) {
            for (x in 0 until width) {
                tiles.add(DungeonTile(GridPosition(x, y), TileType.WALL))
            }
        }
        
        // Create rooms using simple BSP
        val rooms = generateRooms(width, height, 5 + level)
        
        // Carve out rooms
        rooms.forEach { room ->
            for (y in room.y until room.y + room.height) {
                for (x in room.x until room.x + room.width) {
                    val index = y * width + x
                    if (index < tiles.size) {
                        tiles[index] = tiles[index].copy(type = TileType.FLOOR)
                    }
                }
            }
        }
        
        // Connect rooms with corridors
        for (i in 0 until rooms.size - 1) {
            createCorridor(tiles, width, rooms[i], rooms[i + 1])
        }
        
        // Place stairs in last room
        val lastRoom = rooms.last()
        val stairsPos = GridPosition(
            lastRoom.x + lastRoom.width / 2,
            lastRoom.y + lastRoom.height / 2
        )
        val stairsIndex = stairsPos.y * width + stairsPos.x
        if (stairsIndex < tiles.size) {
            tiles[stairsIndex] = tiles[stairsIndex].copy(type = TileType.STAIRS_DOWN)
        }
        
        // Place healing fountains (30% chance per room, skip first and last)
        rooms.drop(1).dropLast(1).forEach { room ->
            if (Random.nextFloat() < 0.3f) {
                val fountainPos = GridPosition(
                    room.x + room.width / 2,
                    room.y + room.height / 2
                )
                val fountainIndex = fountainPos.y * width + fountainPos.x
                if (fountainIndex < tiles.size) {
                    tiles[fountainIndex] = tiles[fountainIndex].copy(type = TileType.HEALING_FOUNTAIN)
                }
            }
        }
        
        // Generate enemies
        val enemies = generateEnemies(rooms, level)
        
        // Generate items
        val items = generateItems(rooms, level)
        
        // Get player spawn position (center of first room)
        val firstRoom = rooms.first()
        val playerSpawn = GridPosition(
            firstRoom.x + firstRoom.width / 2,
            firstRoom.y + firstRoom.height / 2
        )
        
        // Reveal starting area
        val updatedTiles = updateVisibility(tiles, playerSpawn)
        
        return GeneratedDungeon(updatedTiles, enemies, items, playerSpawn)
    }
    
    private data class Room(val x: Int, val y: Int, val width: Int, val height: Int)
    
    private fun generateRooms(width: Int, height: Int, count: Int): List<Room> {
        val rooms = mutableListOf<Room>()
        
        repeat(count) {
            val roomWidth = Random.nextInt(4, 8)
            val roomHeight = Random.nextInt(4, 8)
            val roomX = Random.nextInt(1, width - roomWidth - 1)
            val roomY = Random.nextInt(1, height - roomHeight - 1)
            
            val newRoom = Room(roomX, roomY, roomWidth, roomHeight)
            
            // Check for overlap
            val overlaps = rooms.any { existing ->
                newRoom.x < existing.x + existing.width + 1 &&
                newRoom.x + newRoom.width + 1 > existing.x &&
                newRoom.y < existing.y + existing.height + 1 &&
                newRoom.y + newRoom.height + 1 > existing.y
            }
            
            if (!overlaps) {
                rooms.add(newRoom)
            }
        }
        
        return rooms
    }
    
    private fun createCorridor(tiles: MutableList<DungeonTile>, width: Int, room1: Room, room2: Room) {
        val x1 = room1.x + room1.width / 2
        val y1 = room1.y + room1.height / 2
        val x2 = room2.x + room2.width / 2
        val y2 = room2.y + room2.height / 2
        
        // Horizontal corridor
        val startX = kotlin.math.min(x1, x2)
        val endX = kotlin.math.max(x1, x2)
        for (x in startX..endX) {
            val index = y1 * width + x
            if (index < tiles.size) {
                tiles[index] = tiles[index].copy(type = TileType.FLOOR)
            }
        }
        
        // Vertical corridor
        val startY = kotlin.math.min(y1, y2)
        val endY = kotlin.math.max(y1, y2)
        for (y in startY..endY) {
            val index = y * width + x2
            if (index < tiles.size) {
                tiles[index] = tiles[index].copy(type = TileType.FLOOR)
            }
        }
    }
    
    private fun generateEnemies(rooms: List<Room>, dungeonLevel: Int): List<DungeonEnemy> {
        val enemies = mutableListOf<DungeonEnemy>()
        
        // Expanded enemy roster with (Name, Emoji, BaseHP)
        val enemyTypes = listOf(
            Triple("Rat", "🐀", 15),
            Triple("Spider", "🕷️", 18),
            Triple("Goblin", "👺", 25),
            Triple("Wolf", "🐺", 30),
            Triple("Skeleton", "💀", 35),
            Triple("Zombie", "🧟‍♂️", 40),
            Triple("Orc", "🧟", 45),
            Triple("Troll", "👹", 55),
            Triple("Vampire", "🧛", 65),
            Triple("Dragon", "🐉", 80)
        )
        
        // Skip first room (player spawn)
        rooms.drop(1).forEach { room ->
            // Higher chance of enemies (70%)
            if (Random.nextFloat() < 0.7f) {
                // Choose enemy based on dungeon level (gradually unlock harder enemies)
                val maxEnemyTier = (dungeonLevel + 1).coerceAtMost(enemyTypes.size)
                val enemyType = enemyTypes[Random.nextInt(0, maxEnemyTier)]
                
                val pos = GridPosition(
                    room.x + Random.nextInt(1, room.width - 1),
                    room.y + Random.nextInt(1, room.height - 1)
                )
                
                enemies.add(
                    DungeonEnemy(
                        id = nextEnemyId++,
                        name = enemyType.first,
                        emoji = enemyType.second,
                        position = pos,
                        hp = enemyType.third + (dungeonLevel * 5),
                        maxHp = enemyType.third + (dungeonLevel * 5),
                        attack = 5 + dungeonLevel * 2,
                        defense = 2 + dungeonLevel,
                        xpReward = enemyType.third * 2
                    )
                )
            }
        }
        
        // Spawn boss on level 10 (final level)
        if (dungeonLevel >= 10) {
            val bossRoom = rooms.lastOrNull()
            if (bossRoom != null) {
                val bossPos = GridPosition(
                    bossRoom.x + bossRoom.width / 2,
                    bossRoom.y + bossRoom.height / 2
                )
                
               enemies.add(
                    DungeonEnemy(
                        id = nextEnemyId++,
                        name = "Dragon Lord",
                        emoji = "🐲",
                        position = bossPos,
                        hp = 250,
                        maxHp = 250,
                        attack = 40,
                        defense = 15,
                        xpReward = 500,
                        isAlive = true,
                        isAggressive = true,
                        isBoss = true
                    )
                )
                
                addMessage("⚠️ You feel a powerful presence... A BOSS awaits!")
            }
        }
        
        return enemies
    }
    
    private fun generateItems(rooms: List<Room>, dungeonLevel: Int): List<Item> {
        val items = mutableListOf<Item>()
        
        rooms.forEach { room ->
            if (Random.nextFloat() < 0.4f) {
                val pos = GridPosition(
                    room.x + Random.nextInt(1, room.width - 1),
                    room.y + Random.nextInt(1, room.height - 1)
                )
                
                val itemType = Random.nextInt(0, 4)
                val item = when (itemType) {
                    0 -> Item(nextItemId++, "Health Potion", ItemType.POTION, "❤️", pos, healing = 30)
                    1 -> Item(nextItemId++, "Gold Pile", ItemType.GOLD, "💰", pos, value = Random.nextInt(10, 50))
                    2 -> Item(nextItemId++, "Iron Sword", ItemType.WEAPON, "⚔️", pos, attack = 5 + dungeonLevel * 2)
                    else -> Item(nextItemId++, "Leather Armor", ItemType.ARMOR, "🛡️", pos, defense = 3 + dungeonLevel)
                }
                
                items.add(item)
            }
        }
        
        return items
    }
    
    private data class GeneratedDungeon(
        val tiles: List<DungeonTile>,
        val enemies: List<DungeonEnemy>,
        val items: List<Item>,
        val playerSpawn: GridPosition
    )
    
    // Auto-play mode functions
    fun toggleAutoPlay() {
        val newAutoPlayState = !_state.value.isAutoPlaying
        
        _state.update { it.copy(isAutoPlaying = newAutoPlayState) }
        
        if (newAutoPlayState) {
            addMessage("🤖 Auto-play enabled!")
            startAutoPlay()
        } else {
            addMessage("👤 Manual control restored")
            stopAutoPlay()
        }
    }
    
    private fun startAutoPlay() {
        autoPlayJob?.cancel()
        autoPlayJob = viewModelScope.launch {
            runAutoPlayLoop()
        }
    }
    
    private fun stopAutoPlay() {
        autoPlayJob?.cancel()
        autoPlayJob = null
    }
    
    private suspend fun runAutoPlayLoop() {
        while (_state.value.isAutoPlaying && _state.value.gameState != DungeonGameState.GAME_OVER) {
            val currentState = _state.value
            
            when (currentState.gameState) {
                DungeonGameState.EXPLORING -> {
                    // AI decides next move
                    val direction = autoPlayAgent.decideExplorationMove(currentState)
                    if (direction != null) {
                        movePlayer(direction)
                    }
                    delay(300) // Slower for visibility
                }
                
                DungeonGameState.IN_COMBAT -> {
                    // AI decides combat action
                    val action = autoPlayAgent.decideCombatAction(currentState)
                    when (action) {
                        CombatAction.ATTACK -> attack()
                        CombatAction.DEFEND -> defend()
                        CombatAction.MAGIC -> useMagic()
                        CombatAction.FLEE -> flee()
                    }
                    delay(500) // Wait for combat animations
                }
                
                DungeonGameState.LEVEL_UP -> {
                    // Continue after level up
                    delay(1000)
                    continuePlaying()
                }
                
                else -> {
                    delay(100)
                }
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopAutoPlay()
    }
}
