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
// Classes moved to DungeonModels.kt

class ClassicDungeonViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(ClassicDungeonState())
    val state = _state.asStateFlow()
    
    private var nextItemId = 0
    private var nextEnemyId = 0
    private val autoPlayAgent = AutoPlayAgent()
    private var autoPlayJob: kotlinx.coroutines.Job? = null
    private val dungeonGenerator = DungeonGenerator()
    
    fun startNewGame() {
        nextItemId = 0
        nextEnemyId = 0
        
        val dungeon = dungeonGenerator.generateDungeon(20, 20, 1, nextItemId, nextEnemyId)
        nextItemId = dungeon.nextItemId
        nextEnemyId = dungeon.nextEnemyId

        _state.update { 
            ClassicDungeonState(
                gameState = DungeonGameState.CLASS_SELECTION,
                dungeonLevel = 1,
                tiles = dungeon.tiles,
                player = Player(position = dungeon.playerSpawn),
                enemies = dungeon.enemies,
                items = dungeon.items,
                messages = listOf("Initializing simulation...", "Select your avatar."),
                turnCount = 0,
                gameSpeed = 1.0f // Default 1x speed
            )
        }
    }
    
    fun setGameSpeed(speed: Float) {
        _state.update { it.copy(gameSpeed = speed) }
    }
    
    fun exitToMenu() {
        stopAutoPlay()
        _state.update { it.copy(gameState = DungeonGameState.CLASS_SELECTION) }
    }
    
    fun chooseClass(selectedClass: PlayerClass) {
        _state.update { state ->
            val basePlayer = state.player
            
            // Apply Class Stats
            val classedPlayer = when (selectedClass) {
                PlayerClass.NEON_LICH -> basePlayer.copy(
                    maxHp = 60, hp = 60,
                    attack = 4, defense = 2,
                    maxMana = 150, mana = 150,
                    playerClass = selectedClass,
                    inventory = listOf(Item(nextItemId++, "Data Phylactery", ItemType.POTION, "💾", healing = 50))
                )
                PlayerClass.FLESH_WEAVER -> basePlayer.copy(
                    maxHp = 120, hp = 120,
                    attack = 8, defense = 4,
                    maxMana = 60, mana = 60,
                    playerClass = selectedClass,
                    inventory = listOf(Item(nextItemId++, "Bio-Wand", ItemType.WEAPON, "🦴", attack = 7))
                )
                PlayerClass.QUANTUM_GAMBLER -> basePlayer.copy(
                    maxHp = 90, hp = 90,
                    attack = 10, defense = 3,
                    maxMana = 40, mana = 40,
                    playerClass = selectedClass,
                    inventory = listOf(Item(nextItemId++, "Loaded Dice", ItemType.WEAPON, "🎲", attack = 8, critChance=0.10f))
                )
                PlayerClass.CHRONO_KNIGHT -> basePlayer.copy(
                    maxHp = 130, hp = 130,
                    attack = 9, defense = 6,
                    maxMana = 50, mana = 50,
                    playerClass = selectedClass,
                    inventory = listOf(Item(nextItemId++, "Time Blade", ItemType.WEAPON, "🗡️", attack = 6))
                )
                PlayerClass.VOID_STALKER -> basePlayer.copy(
                    maxHp = 70, hp = 70,
                    attack = 14, defense = 2,
                    maxMana = 30, mana = 30,
                    playerClass = selectedClass,
                    inventory = listOf(Item(nextItemId++, "Void Dagger", ItemType.WEAPON, "🗡️", attack = 10, critChance=0.15f))
                )
                PlayerClass.PLASMA_PALADIN -> basePlayer.copy(
                    maxHp = 150, hp = 150,
                    attack = 7, defense = 8,
                    maxMana = 40, mana = 40,
                    playerClass = selectedClass,
                    inventory = listOf(Item(nextItemId++, "Energy Shield", ItemType.WEAPON, "🛡️", attack = 4, defense=5))
                )
            }
            
            state.copy(
                gameState = DungeonGameState.EXPLORING,
                player = classedPlayer,
                messages = listOf("Avatar ${selectedClass.title} uploaded. Good luck.")
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
        
        // Move player
        _state.update { state ->
            val updatedState = state.copy(
                player = state.player.copy(position = newPos),
                // Reveal and update visibility
                // Update fog of war
                tiles = dungeonGenerator.updateVisibility(state.tiles, newPos)
            )
            
            updatedState
        }
        
        // Check for interactions based on tile type
        val currentTile = currentState.tiles.find { it.position == newPos }
        if (currentTile != null) {
            when (currentTile.type) {
                TileType.STAIRS_DOWN -> {
                    descendStairs()
                    return
                }
                TileType.STAIRS_UP -> {
                    addMessage("Stairs up are blocked by rubble.")
                }
                TileType.SHOP -> {
                     enterShop()
                }
                TileType.INN -> {
                     enterInn()
                }
                TileType.HEALING_FOUNTAIN -> {
                    // Existing fountain logic could remain or merge into INN, keep for now as free mini-heal
                    val healAmount = (currentState.player.maxHp * 0.3).toInt()
                    addMessage("💧 You drink from the fountain. +$healAmount HP.")
                    _state.update { s -> 
                        // Remove fountain effect (turn to floor)
                        s.copy(
                            player = s.player.copy(hp = (s.player.hp + healAmount).coerceAtMost(s.player.maxHp)),
                            tiles = s.tiles.map { if (it.position == newPos) it.copy(type = TileType.FLOOR) else it }
                        )
                    }
                }
                else -> {}
            }
        }
        
        // Items pickup
        val itemOnTile = currentState.items.find { it.position == newPos }
        if (itemOnTile != null) {
            pickupItem(itemOnTile.id)
        }
        
        // Decrement Buffs
        _state.update { s ->
            val updatedBuffs = s.player.activeBuffs.map { it.copy(duration = it.duration - 1) }.filter { it.duration > 0 }
            if (updatedBuffs.size < s.player.activeBuffs.size) {
                 // Some buffs expired
            }
            s.copy(
                turnCount = s.turnCount + 1,
                player = s.player.copy(activeBuffs = updatedBuffs)
            )
        }
        
        processEnemyTurns()
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
                else -> {
                    // Auto-equip logic if default items are bad
                    // For now, just pick it up
                    addMessage("📦 Picked up ${item.emoji} ${item.name} [${item.rarity}]")
                    var newState = state.player.copy(inventory = state.player.inventory + item)
                    
                    // Simple Auto-Equip Logic for Idle Mode
                    if (state.isAutoPlaying) {
                        val player = newState
                        // Check if item is better than equipped
                        val shouldEquip = when (item.type) {
                            ItemType.WEAPON -> (item.attack + item.magic) > (player.equippedWeapon?.let { it.attack + it.magic } ?: 0)
                            ItemType.ARMOR -> item.defense > (player.equippedArmor?.defense ?: 0)
                            ItemType.HELMET -> (item.defense + item.magic) > (player.equippedHelmet?.let { it.defense + it.magic } ?: 0)
                            ItemType.BOOTS -> (item.defense + (item.dodgeChance*100).toInt()) > (player.equippedBoots?.let { it.defense + (it.dodgeChance*100).toInt() } ?: 0)
                            ItemType.ACCESSORY -> (item.magic + item.attack) > (player.equippedAccessory?.let { it.magic + it.attack } ?: 0)
                            else -> false
                        }
                        
                        if (shouldEquip) {
                            // Find the item again in the new inventory to equip it properly with the viewmodel function logic
                            // But here we are inside update block, so let's just do it directly
                            // Remove from inventory, set as equipped, put old back
                            val inventoryWithoutItem = newState.inventory.filter { it.id != item.id }
                            
                            newState = when (item.type) {
                                ItemType.WEAPON -> newState.copy(equippedWeapon = item, inventory = inventoryWithoutItem + listOfNotNull(player.equippedWeapon))
                                ItemType.ARMOR -> newState.copy(equippedArmor = item, inventory = inventoryWithoutItem + listOfNotNull(player.equippedArmor))
                                ItemType.HELMET -> newState.copy(equippedHelmet = item, inventory = inventoryWithoutItem + listOfNotNull(player.equippedHelmet))
                                ItemType.BOOTS -> newState.copy(equippedBoots = item, inventory = inventoryWithoutItem + listOfNotNull(player.equippedBoots))
                                ItemType.ACCESSORY -> newState.copy(equippedAccessory = item, inventory = inventoryWithoutItem + listOfNotNull(player.equippedAccessory))
                                else -> newState
                            }
                            addMessage("⚡ Auto-equipped ${item.emoji} ${item.name}")
                        }
                    }
                    newState
                }
            }
            
            state.copy(
                player = updatedPlayer,
                items = state.items.filter { it.id != itemId }
            )
        }
    }

    fun useItem(item: Item) {
        _state.update { state ->
            if (item.type == ItemType.POTION) {
                val healAmount = item.healing.coerceAtMost(state.player.maxHp - state.player.hp)
                addMessage("❤️ Used ${item.name} for $healAmount HP")
                state.copy(
                    player = state.player.copy(
                        hp = (state.player.hp + healAmount).coerceAtMost(state.player.maxHp),
                        inventory = state.player.inventory.filter { it.id != item.id }
                    )
                )
            } else {
                state
            }
        }
    }
    
    fun attack() {
        val currentState = _state.value
        val enemy = currentState.currentEnemy ?: return
        
        if (currentState.gameState != DungeonGameState.IN_COMBAT) return
        
        // 1. Check for Enemy Dodge (Small base chance + randomness)
        if (Random.nextFloat() < 0.05f) {
            addMessage("💨 ${enemy.name} dodged your attack!")
            // Enemy counter-attack immediately
            viewModelScope.launch {
                delay(300)
                enemyAttack(enemy)
            }
            return
        }

        // 2. Check for Player Critical Hit
        val isCrit = Random.nextFloat() < currentState.player.getTotalCrit()
        var damage = calculateDamage(currentState.player.getTotalAttack(), enemy.defense)
        if (isCrit) {
            damage = (damage * 1.5).toInt()
        }
        
        val newEnemyHp = (enemy.hp - damage).coerceAtLeast(0)
        
        _state.update { state ->
            val updatedEnemy = enemy.copy(hp = newEnemyHp, isAlive = newEnemyHp > 0)
            
            state.copy(
                currentEnemy = updatedEnemy,
                enemies = state.enemies.map { if (it.id == enemy.id) updatedEnemy else it }
            )
        }
        
        if (isCrit) {
            val critMsg = when(currentState.player.playerClass) {
                PlayerClass.NEON_LICH -> "👾 SYSTEM CRASH! Neural shock burns ${enemy.name}!"
                PlayerClass.FLESH_WEAVER -> "🥩 BUTCHERED! Minions feast on ${enemy.name}!"
                PlayerClass.QUANTUM_GAMBLER -> "🎲 JACKPOT! Reality collapses on ${enemy.name}!"
                PlayerClass.CHRONO_KNIGHT -> "⏳ TIME BREAK! You hit ${enemy.name} twice!"
                PlayerClass.VOID_STALKER -> "🥷 ASSASSINATED! ${enemy.name} never saw it coming!"
                PlayerClass.PLASMA_PALADIN -> "🛡️ SMITE! Holy plasma disintegrates ${enemy.name}!"
            }
            addMessage("💥 $critMsg ($damage dmg)")
        } else {
            val atkMsg = when(currentState.player.playerClass) {
                PlayerClass.NEON_LICH -> "You drain code from"
                PlayerClass.FLESH_WEAVER -> "Your flesh-hulk smashes"
                PlayerClass.QUANTUM_GAMBLER -> "You phase-strike"
                PlayerClass.CHRONO_KNIGHT -> "You slash"
                PlayerClass.VOID_STALKER -> "You stab"
                PlayerClass.PLASMA_PALADIN -> "You hammer"
            }
            addMessage("⚔️ $atkMsg ${enemy.name} for $damage damage!")
        }
        
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
        
        val defMsg = when(currentState.player.playerClass) {
            PlayerClass.NEON_LICH -> "🛡️ You raise a firewall!"
            PlayerClass.FLESH_WEAVER -> "🛡️ A wall of meat absorbs the blow!"
            PlayerClass.QUANTUM_GAMBLER -> "🛡️ You flicker out of reality!"
            PlayerClass.CHRONO_KNIGHT -> "🛡️ You rewind the impact!"
            PlayerClass.VOID_STALKER -> "🛡️ You vanish into shadows!"
            PlayerClass.PLASMA_PALADIN -> "🛡️ Energy shields holding!"
        }
        addMessage(defMsg)
        
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
    
    fun useAbility() {
        val currentState = _state.value
        val enemy = currentState.currentEnemy ?: return
        
        val (abilityName, manaCost) = currentState.player.playerClass.getAbilityInfo()
        
        if (currentState.player.mana < manaCost) {
            addMessage("❌ Not enough mana for $abilityName!")
            return
        }
        
        // Consume Mana
        _state.update { it.copy(player = it.player.copy(mana = it.player.mana - manaCost)) }
        
        // Execute Unique Ability Logic
        when (currentState.player.playerClass) {
            PlayerClass.NEON_LICH -> {
                // High Damage + Lifesteal
                val damage = (currentState.player.getTotalMagic() * 2.5).toInt()
                val heal = damage / 2
                applyDamage(enemy, damage, "Data Rot")
                healPlayer(heal)
                addMessage("💚 Drained $heal HP from code!")
            }
            PlayerClass.FLESH_WEAVER -> {
                // Massive Area Damage (Single target simulates blasting minions at it)
                val damage = (currentState.player.getTotalAttack() * 3.0).toInt()
                applyDamage(enemy, damage, "Corpse Explosion")
                // Recoil damage
                applyPlayerDamage(5)
                addMessage("🩸 Sacrificed a minion for damage!")
            }
            PlayerClass.QUANTUM_GAMBLER -> {
                 // Random Effect
                 val roll = Random.nextFloat()
                 if (roll < 0.3f) {
                     addMessage("🎲 BAD LUCK! You tripped.")
                     applyPlayerDamage(5)
                 } else if (roll < 0.9f) {
                     val damage = (currentState.player.getTotalAttack() * 2).toInt()
                     applyDamage(enemy, damage, "Lucky Shot")
                 } else {
                     // Nerfed from Instant Kill to 5x Damage
                     val damage = (currentState.player.getTotalAttack() * 5).toInt()
                     applyDamage(enemy, damage, "JACKPOT")
                     addMessage("🎰 JACKPOT! MASSIVE HIT!")
                 }
            }
            PlayerClass.CHRONO_KNIGHT -> {
                // Heal back to full or undo last turn (Simple: Massive Heal + Small Damage)
                val dmg = currentState.player.getTotalAttack()
                healPlayer(30)
                applyDamage(enemy, dmg, "Rewind Strike")
                addMessage("⏳ Rewound time to heal wounds!")
            }
            PlayerClass.VOID_STALKER -> {
                // Execute low HP or massive crit
                val hpPercent = enemy.hp.toFloat() / enemy.maxHp
                if (hpPercent < 0.3f) {
                    // Nerfed from Instant Kill to 4x Damage Execute
                    val damage = (currentState.player.getTotalAttack() * 4).toInt()
                    applyDamage(enemy, damage, "EXECUTE")
                    addMessage("☠️ EXECUTED STRIKE!")
                } else {
                    val damage = (currentState.player.getTotalAttack() * 2.5).toInt()
                    applyDamage(enemy, damage, "Backstab")
                }
            }
            PlayerClass.PLASMA_PALADIN -> {
                // Damage + Shield (Defense Up)
                val damage = (currentState.player.getTotalMagic() * 1.5).toInt()
                applyDamage(enemy, damage, "Holy Nova")
                // Perm boost for fight? Or just heal. Let's do heal for now strictly.
                // Or defense buff logic isn't fully robust yet, so just heal.
                healPlayer(15)
                addMessage("🛡️ Shields restored!")
            }
        }
        
        // Enemy Turn Check (if still alive)
        val afterState = _state.value
        val afterEnemy = afterState.currentEnemy
        if (afterEnemy != null && afterEnemy.isAlive) {
             viewModelScope.launch {
                delay(500)
                enemyAttack(afterEnemy)
            }
        }
    }

    private fun applyDamage(enemy: DungeonEnemy, damage: Int, source: String) {
        val newEnemyHp = (enemy.hp - damage).coerceAtLeast(0)
        
        _state.update { state ->
            val updatedEnemy = enemy.copy(hp = newEnemyHp, isAlive = newEnemyHp > 0)
            state.copy(
                currentEnemy = updatedEnemy,
                enemies = state.enemies.map { if (it.id == enemy.id) updatedEnemy else it }
            )
        }
        
        addMessage("✨ $source hits ${enemy.name} for $damage!")
        
        if (newEnemyHp <= 0) {
            onEnemyDefeated(enemy)
        }
    }

    private fun healPlayer(amount: Int) {
        _state.update { s ->
            s.copy(player = s.player.copy(hp = (s.player.hp + amount).coerceAtMost(s.player.maxHp)))
        }
    }
    
    // Used for self-inflicted damage or traps
    private fun applyPlayerDamage(amount: Int) {
         _state.update { s ->
            s.copy(player = s.player.copy(hp = (s.player.hp - amount).coerceAtLeast(0)))
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
                ItemType.HELMET -> {
                    val old = state.player.equippedHelmet
                    var inventory = state.player.inventory.filter { it.id != item.id }
                    if (old != null) inventory = inventory + old
                    addMessage("🛡️ Equipped ${item.name}")
                    state.copy(player = state.player.copy(equippedHelmet = item, inventory = inventory))
                }
                ItemType.BOOTS -> {
                    val old = state.player.equippedBoots
                    var inventory = state.player.inventory.filter { it.id != item.id }
                    if (old != null) inventory = inventory + old
                    addMessage("👢 Equipped ${item.name}")
                    state.copy(player = state.player.copy(equippedBoots = item, inventory = inventory))
                }
                ItemType.ACCESSORY -> {
                    val old = state.player.equippedAccessory
                    var inventory = state.player.inventory.filter { it.id != item.id }
                    if (old != null) inventory = inventory + old
                    addMessage("💍 Equipped ${item.name}")
                    state.copy(player = state.player.copy(equippedAccessory = item, inventory = inventory))
                }
                else -> state
            }
        }
    }

    fun unequipItem(type: ItemType) {
        _state.update { state ->
            val player = state.player
            val newPlayer = when (type) {
                ItemType.WEAPON -> player.equippedWeapon?.let { player.copy(equippedWeapon = null, inventory = player.inventory + it) }
                ItemType.ARMOR -> player.equippedArmor?.let { player.copy(equippedArmor = null, inventory = player.inventory + it) }
                ItemType.HELMET -> player.equippedHelmet?.let { player.copy(equippedHelmet = null, inventory = player.inventory + it) }
                ItemType.BOOTS -> player.equippedBoots?.let { player.copy(equippedBoots = null, inventory = player.inventory + it) }
                ItemType.ACCESSORY -> player.equippedAccessory?.let { player.copy(equippedAccessory = null, inventory = player.inventory + it) }
                else -> null
            }
            
            if (newPlayer != null) state.copy(player = newPlayer) else state
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
        
        // 1. Check for Player Dodge
        val dodgeChance = currentState.player.getTotalDodge()
        if (Random.nextFloat() < dodgeChance) {
            addMessage("💨 You dodged ${enemy.name}'s attack!")
            return
        }
        
        // 2. Enemy Critical Hit (5% chance)
        val isCrit = Random.nextFloat() < 0.05f
        var damage = calculateDamage(enemy.attack, currentState.player.getTotalDefense())
        if (isCrit) {
            damage = (damage * 1.5).toInt()
        }
        
        val newHp = (currentState.player.hp - damage).coerceAtLeast(0)
        
        _state.update { state ->
            state.copy(player = state.player.copy(hp = newHp))
        }
        
        if (isCrit) {
            addMessage("😫 OUCH! ${enemy.name} landed a CRITICAL HIT for $damage damage!")
        } else {
            addMessage("💥 ${enemy.name} hits you for $damage damage!")
        }
        
        if (newHp <= 0) {
            gameOver()
        }
    }
    
    private fun onEnemyDefeated(enemy: DungeonEnemy) {
        val xpGained = enemy.xpReward
        val goldDrop = Random.nextInt(5, 20)
        
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
            
            // Check if boss was defeated (victory!)
            val finalGameState = if (enemy.isBoss) {
                DungeonGameState.VICTORY
            } else newGameState

            // Generate loot (50% chance for loot, 100% for bosses)
            val lootItem = if (enemy.isBoss || Random.nextFloat() < 0.5f) {
                dungeonGenerator.generateLootItem(state.dungeonLevel, enemy.isBoss)
            } else null
            
            val updatedInventory = if (lootItem != null) {
                updatedPlayer.inventory + lootItem
            } else updatedPlayer.inventory

            state.copy(
                player = updatedPlayer.copy(inventory = updatedInventory),
                gameState = finalGameState,
                currentEnemy = null,
                enemies = state.enemies.map { if (it.id == enemy.id) it.copy(isAlive = false) else it },
                items = state.items // Items on ground are not affected by enemy defeat
            )
        }
        
        addMessage("🎉 Defeated ${enemy.name}! +$xpGained XP, +$goldDrop gold")
        
        // The loot message is now handled inside the update block for consistency
        // with the inventory update.
        val currentState = _state.value
        val lastMessage = currentState.messages.lastOrNull()
        if (lastMessage != null && lastMessage.startsWith("🎁 Loot!")) {
            addMessage(lastMessage) // Re-add if it was a loot message
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
        val nextLevel = _state.value.dungeonLevel + 1
        
        val dungeon = dungeonGenerator.generateDungeon(20 + nextLevel, 20 + nextLevel, nextLevel, nextItemId, nextEnemyId)
        nextItemId = dungeon.nextItemId
        nextEnemyId = dungeon.nextEnemyId
        
        _state.update { 
            it.copy(
                dungeonLevel = nextLevel,
                tiles = dungeon.tiles,
                player = it.player.copy(position = dungeon.playerSpawn),
                enemies = dungeon.enemies,
                items = dungeon.items,
                shopItems = emptyList(), // Clear shop items
                messages = it.messages + "⬇️ Descended to Level $nextLevel",
                currentEnemy = null // Ensure combat ends
            ) 
        }
        
        addMessage("New depth reached. The air gets colder...")
        
        // Auto-save?
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
    
    // Loot generation and Visibility logic moved to DungeonGenerator.kt
    
    private fun addMessage(message: String) {
        _state.update {
            it.copy(messages = (it.messages + message).takeLast(5))
        }
    }
    
    fun enterShop() {
        stopAutoPlay()
        val shopInventory = List(5) { 
            dungeonGenerator.generateRandomItem(_state.value.dungeonLevel + 1) // Slightly better items
        }
        _state.update { 
            it.copy(
                gameState = DungeonGameState.SHOPPING, 
                shopItems = shopInventory,
                messages = it.messages + "💰 Entered General Store"
            ) 
        }
    }

    fun buyItem(item: Item) {
        val currentState = _state.value
        if (currentState.player.gold >= item.value) {
            _state.update { s ->
                s.copy(
                    player = s.player.copy(
                        gold = s.player.gold - item.value,
                        inventory = s.player.inventory + item
                    ),
                    shopItems = s.shopItems.filter { it.id != item.id }, // Remove bought item? Or keep indefinite stock? Remove for now.
                    messages = s.messages + "Bought ${item.name} for ${item.value}g"
                )
            }
        } else {
            addMessage("❌ Not enough gold!")
        }
    }
    
    fun sellItem(item: Item) {
        val sellValue = item.value / 2
        _state.update { s ->
            s.copy(
                player = s.player.copy(
                    gold = s.player.gold + sellValue,
                    inventory = s.player.inventory.filter { it.id != item.id }
                ),
                messages = s.messages + "Sold ${item.name} for ${sellValue}g"
            )
        }
    }

    fun enterInn() {
        stopAutoPlay()
        _state.update { it.copy(gameState = DungeonGameState.RESTING, messages = it.messages + "🏨 Entered The Cozy Inn") }
    }
    
    fun restAtInn(type: String) {
        val currentState = _state.value
        val cost = when(type) {
            "Basic" -> 10
            "Luxury" -> 50
            "Feast" -> 100
            else -> 0
        }
        
        if (currentState.player.gold < cost) {
            addMessage("❌ Not enough gold!")
            return
        }
        
        when(type) {
            "Basic" -> {
                 _state.update { s ->
                     s.copy(
                         player = s.player.copy(
                             gold = s.player.gold - cost,
                             hp = (s.player.hp + s.player.maxHp/2).coerceAtMost(s.player.maxHp),
                             mana = (s.player.mana + s.player.maxMana/2).coerceAtMost(s.player.maxMana)
                         ),
                         messages = s.messages + "💤 Rested. HP/MP Restored."
                     )
                 }
            }
            "Luxury" -> {
                 val buff = Buff("Well Rested", "All stats +10%", 50, 0.1f, 0.1f, 0.1f)
                 _state.update { s ->
                     s.copy(
                         player = s.player.copy(
                             gold = s.player.gold - cost,
                             hp = s.player.maxHp,
                             mana = s.player.maxMana,
                             activeBuffs = s.player.activeBuffs + buff
                         ),
                         messages = s.messages + "🛁 Luxury Bath! Full Heal + Buff!"
                     )
                 }
            }
            "Feast" -> {
                 val buff = Buff("Berzerk", "Attack +30%", 30, 0.3f, 0.0f, 0.0f)
                 _state.update { s ->
                     s.copy(
                         player = s.player.copy(
                             gold = s.player.gold - cost,
                             hp = s.player.maxHp,
                             mana = s.player.maxMana,
                             activeBuffs = s.player.activeBuffs + buff
                         ),
                         messages = s.messages + "🍗 Warrior's Feast! Attack UP!"
                     )
                 }
            }
        }
    }
    
    fun leaveBuilding() {
        _state.update { it.copy(gameState = DungeonGameState.EXPLORING) }
    }

    // Dungeon generation moved to DungeonGenerator.kt
    
    // Debug Functions
    fun openDebugMenu() {
        stopAutoPlay()
        _state.update { it.copy(gameState = DungeonGameState.DEBUG_MENU) }
    }
    
    fun debugForceShop() {
        enterShop()
    }
    
    fun debugGiveGold(amount: Int) {
        _state.update { s -> 
            s.copy(
                player = s.player.copy(gold = s.player.gold + amount),
                messages = s.messages + "🔧 Debug: Added $amount gold"
            ) 
        }
    }
    
    fun debugLevelUp() {
        _state.update { s ->
            // Max out XP then trigger level up
            val readyPlayer = s.player.copy(xp = s.player.xpToNextLevel)
            val newPlayer = levelUp(readyPlayer)
            
            s.copy(
                player = newPlayer,
                gameState = DungeonGameState.LEVEL_UP,
                messages = s.messages + "🔧 Debug: Force Level Up"
            )
        }
    }
    
    fun debugSpawnEnemy() {
         // Find a free tile near player
         val playerPos = _state.value.player.position
         val freeTile = _state.value.tiles.firstOrNull { 
             it.type == TileType.FLOOR && 
             it.position != playerPos && 
             _state.value.enemies.none { e -> e.position == it.position }
         }
         
         if (freeTile != null) {
             val enemy = DungeonEnemy(
                 id = Random.nextInt(),
                 name = "Debug Dummy",
                 emoji = "👾",
                 position = freeTile.position,
                 hp = 50,
                 maxHp = 50,
                 attack = 5,
                 defense = 0,
                 xpReward = 100
             )
             _state.update { 
                 it.copy(
                     enemies = it.enemies + enemy,
                     messages = it.messages + "🔧 Debug: Spawned Dummy"
                 ) 
             }
         }
    }

    // Auto-play mode functions
    fun toggleAutoPlay() {
        val newState = !_state.value.isAutoPlaying
        _state.update { it.copy(isAutoPlaying = newState) }
        
        if (newState) {
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
                    val delayTime = (300 / currentState.gameSpeed).toLong().coerceAtLeast(10)
                    delay(delayTime)
                }
                
                DungeonGameState.IN_COMBAT -> {
                    // AI decides combat action
                    val action = autoPlayAgent.decideCombatAction(currentState)
                    when (action) {
                        CombatAction.ATTACK -> attack()
                        CombatAction.DEFEND -> defend()
                        CombatAction.MAGIC -> useAbility()
                        CombatAction.FLEE -> attack() // Never flee in idle mode, fight to the death!
                    }
                    val delayTime = (500 / currentState.gameSpeed).toLong().coerceAtLeast(10)
                    delay(delayTime)
                }
                
                DungeonGameState.LEVEL_UP -> {
                    // fast continue
                    val delayTime = (1000 / currentState.gameSpeed).toLong().coerceAtLeast(50)
                    delay(delayTime)
                    continuePlaying()
                }
                
                DungeonGameState.VICTORY -> {
                     if (currentState.dungeonLevel >= 50) {
                         // True Victory
                         stopAutoPlay()
                         addMessage("🏆 MISSION ACCOMPLISHED. ENTROPY KING DEFEATED.")
                     } else {
                         // Infinite Dungeon descend
                         val delayTime = (2000 / currentState.gameSpeed).toLong().coerceAtLeast(100)
                         delay(delayTime)
                         descendStairs()
                     }
                }
                
                DungeonGameState.GAME_OVER -> {
                    // Stop on death for now, or maybe auto-restart later
                    stopAutoPlay()
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
