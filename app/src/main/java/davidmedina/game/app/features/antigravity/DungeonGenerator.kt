package davidmedina.game.app.features.antigravity

import kotlin.random.Random

data class GeneratedDungeon(
    val tiles: List<DungeonTile>,
    val enemies: List<DungeonEnemy>,
    val items: List<Item>,
    val playerSpawn: GridPosition,
    val nextItemId: Int,
    val nextEnemyId: Int
)

class DungeonGenerator {

    private data class Room(val x: Int, val y: Int, val width: Int, val height: Int)
    
    // Counters to ensure unique IDs during generation
    private var genNextItemId = 0
    private var genNextEnemyId = 0

    fun generateDungeon(width: Int, height: Int, level: Int, startItemId: Int, startEnemyId: Int): GeneratedDungeon {
        genNextItemId = startItemId
        genNextEnemyId = startEnemyId
        
        var tiles = mutableListOf<DungeonTile>()
        
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
        val enemies = mutableListOf<DungeonEnemy>()
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
        
        rooms.drop(1).forEach { room ->
            if (Random.nextFloat() < 0.7f) {
                val maxEnemyTier = (level + 1).coerceAtMost(enemyTypes.size)
                val enemyType = enemyTypes[Random.nextInt(0, maxEnemyTier)]
                
                val pos = GridPosition(
                    room.x + Random.nextInt(1, room.width - 1),
                    room.y + Random.nextInt(1, room.height - 1)
                )
                
                enemies.add(
                    DungeonEnemy(
                        id = genNextEnemyId++,
                        name = enemyType.first,
                        emoji = enemyType.second,
                        position = pos,
                        hp = enemyType.third + (level * 5),
                        maxHp = enemyType.third + (level * 5),
                        attack = 5 + level * 2,
                        defense = 2 + level,
                        xpReward = enemyType.third * 2
                    )
                )
            }
        }
        
        // Spawn boss
        if (level % 10 == 0) {
            val bossRoom = rooms.lastOrNull()
            if (bossRoom != null) {
                val bossPos = GridPosition(
                    bossRoom.x + bossRoom.width / 2,
                    bossRoom.y + bossRoom.height / 2
                )
                val bossName = if (level >= 50) "The Entropy King" else "Level $level Guardian"
                enemies.add(
                    DungeonEnemy(
                        id = genNextEnemyId++,
                        name = bossName,
                        emoji = if (level >= 50) "👑" else "🐲",
                        position = bossPos,
                        hp = 250 + (level * 10),
                        maxHp = 250 + (level * 10),
                        attack = 30 + (level * 3),
                        defense = 10 + level,
                        xpReward = 500 * level,
                        isAlive = true,
                        isAggressive = true,
                        isBoss = true
                    )
                )
            }
        }

        // Generate items on floor
        val items = mutableListOf<Item>()
        rooms.forEach { room ->
            if (Random.nextFloat() < 0.5f) {
                val pos = GridPosition(
                    room.x + Random.nextInt(1, room.width - 1),
                    room.y + Random.nextInt(1, room.height - 1)
                )
                items.add(generateRandomItem(level, pos))
            }
        }
        
        // Place Shop and Inn (Guaranteed 1 per level)
        val validRoomsForServices = rooms.drop(1) // Skip spawn room
        if (validRoomsForServices.size >= 2) {
            val shuffledRooms = validRoomsForServices.shuffled()
            val shopRoom = shuffledRooms[0]
            val innRoom = shuffledRooms[1]
            
            // Place Shop in center of room
            val shopPos = GridPosition(shopRoom.x + shopRoom.width/2, shopRoom.y + shopRoom.height/2)
            val shopIndex = shopPos.y * width + shopPos.x
            if (shopIndex < tiles.size) tiles[shopIndex] = tiles[shopIndex].copy(type = TileType.SHOP)
            
            // Place Inn in center of room
            val innPos = GridPosition(innRoom.x + innRoom.width/2, innRoom.y + innRoom.height/2)
            val innIndex = innPos.y * width + innPos.x
            if (innIndex < tiles.size) tiles[innIndex] = tiles[innIndex].copy(type = TileType.INN)
        } else {
             // Fallback for tiny dungeons: Just try to place them linearly
             validRoomsForServices.forEachIndexed { index, room ->
                 val type = if (index % 2 == 0) TileType.SHOP else TileType.INN
                 val pos = GridPosition(room.x + room.width/2, room.y + room.height/2)
                 val idx = pos.y * width + pos.x
                 if (idx < tiles.size) tiles[idx] = tiles[idx].copy(type = type)
             }
        }
        
        val firstRoom = rooms.first()
        val playerSpawn = GridPosition(
            firstRoom.x + firstRoom.width / 2,
            firstRoom.y + firstRoom.height / 2
        )
        
        val updatedTiles = updateVisibility(tiles, playerSpawn)
        
        return GeneratedDungeon(updatedTiles, enemies, items, playerSpawn, genNextItemId, genNextEnemyId)
    }

    private fun generateRooms(width: Int, height: Int, count: Int): List<Room> {
        val rooms = mutableListOf<Room>()
        repeat(count) {
            val roomWidth = Random.nextInt(4, 8)
            val roomHeight = Random.nextInt(4, 8)
            val roomX = Random.nextInt(1, width - roomWidth - 1)
            val roomY = Random.nextInt(1, height - roomHeight - 1)
            val newRoom = Room(roomX, roomY, roomWidth, roomHeight)
            
            val overlaps = rooms.any { existing ->
                newRoom.x < existing.x + existing.width + 1 &&
                newRoom.x + newRoom.width + 1 > existing.x &&
                newRoom.y < existing.y + existing.height + 1 &&
                newRoom.y + newRoom.height + 1 > existing.y
            }
            if (!overlaps) rooms.add(newRoom)
        }
        return rooms
    }
    
    private fun createCorridor(tiles: MutableList<DungeonTile>, width: Int, room1: Room, room2: Room) {
        val x1 = room1.x + room1.width / 2
        val y1 = room1.y + room1.height / 2
        val x2 = room2.x + room2.width / 2
        val y2 = room2.y + room2.height / 2
        
        val startX = kotlin.math.min(x1, x2)
        val endX = kotlin.math.max(x1, x2)
        for (x in startX..endX) {
            val index = y1 * width + x
            if (index < tiles.size) tiles[index] = tiles[index].copy(type = TileType.FLOOR)
        }
        
        val startY = kotlin.math.min(y1, y2)
        val endY = kotlin.math.max(y1, y2)
        for (y in startY..endY) {
            val index = y * width + x2
            if (index < tiles.size) tiles[index] = tiles[index].copy(type = TileType.FLOOR)
        }
    }
    
    fun updateVisibility(tiles: List<DungeonTile>, playerPos: GridPosition): List<DungeonTile> {
        return tiles.map { tile ->
            if (tile.position.distanceTo(playerPos) <= 5) {
                tile.copy(isRevealed = true, isVisible = true)
            } else {
                tile.copy(isVisible = false)
            }
        }
    }

    // Public method for Loot Generation
    fun generateLootItem(level: Int, isBoss: Boolean): Item {
        // Increment external ID if used from outside context? 
        // We might need to handle IDs better. For now use random ID for loot to avoid collision with generation flow.
        val id = Random.nextInt(100000, 999999)
        
        val rarity = if (isBoss) {
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
        
        val isEquipment = Random.nextFloat() < 0.7f
        val dummyPos = null // Loot items in inventory have no position
        
        return if (isEquipment) {
             generateEquipment(id, level, rarity, dummyPos)
        } else {
             generateConsumable(id, level, rarity, dummyPos)
        }
    }

    // Internal helper for Map Item Generation
    fun generateRandomItem(level: Int, pos: GridPosition? = null): Item {
        // This is used for Floor Items and Shops
        // Internal usage during dungeon gen uses tracked ID. External uses random or we should pass ID.
        // For simplicity: during dungeon gen -> use genNextItemId. External -> use random.
        // BUT wait, if I call this from Shop, I want unique IDs.
        // I'll make this generate a random ID if called from outside context (pos == null usually implies shop/loot?)
        // Actually, let's just use random IDs for all items to simplify. Collisions are rare enough.
        
        val id = if (pos != null) genNextItemId++ else Random.nextInt(1000000) 

        val rarity = when (Random.nextFloat()) {
            in 0f..0.05f -> "Legendary"
            in 0.05f..0.15f -> "Epic"
            in 0.15f..0.40f -> "Rare"
            else -> "Common"
        }
        val isEquipment = Random.nextFloat() < 0.7f
        
        return if (isEquipment) {
             generateEquipment(id, level, rarity, pos)
        } else {
             generateConsumable(id, level, rarity, pos)
        }
    }
    
    private fun generateEquipment(id: Int, level: Int, rarity: String, pos: GridPosition?): Item {
        val isWeapon = Random.nextBoolean()
        
        // Sci-Fi / Dark Fantasy Names
        val prefixes = listOf("Flame", "Frost", "Shadow", "Holy", "Cursed", "Ancient", "Blessed", "Demonic", "Dragon", "Phantom", "Storm", "Void")
        val suffixes = listOf("Slayer", "Bane", "Guard", "Strike", "Wrath", "Fury", "Doom", "Grace", "Might", "Edge", "Soul", "Fang")
        
        val weapons = listOf("Sword", "Axe", "Dagger", "Spear", "Mace", "Blade")
        val armors = listOf("Helm", "Plate", "Shield", "Gauntlets", "Boots", "Cloak")
        
        val prefix = prefixes.random()
        val suffix = suffixes.random()        
        val baseItemName = if (isWeapon) weapons.random() else armors.random()
        val name = "$prefix $baseItemName of $suffix"
        
        val multiplier = when(rarity) {
             "Legendary" -> 2.0f
             "Epic" -> 1.5f
             "Rare" -> 1.2f
             else -> 1.0f
        }
        
        // Determine stats
        val type = if (isWeapon) ItemType.WEAPON else {
            val roll = Random.nextInt(4)
            when(roll) {
                0 -> ItemType.ARMOR
                1 -> ItemType.HELMET
                2 -> ItemType.BOOTS
                else -> ItemType.ACCESSORY
            }
        }
        
        val emoji = when(type) {
            ItemType.WEAPON -> "🗡️"
            ItemType.ARMOR -> "🛡️"
            ItemType.HELMET -> "🥽"
            ItemType.BOOTS -> "👢"
            else -> "💍"
        }

        return Item(
            id = id, 
            name = name, 
            type = type, 
            emoji = emoji, 
            position = pos, 
            rarity = rarity,
            value = (100 * multiplier).toInt(),
            attack = if (type == ItemType.WEAPON) (5 + level * 2 * multiplier).toInt() else 0,
            defense = if (type != ItemType.WEAPON && type != ItemType.ACCESSORY) (2 + level * multiplier).toInt() else 0,
            magic = if (type == ItemType.ACCESSORY) (2 + level * multiplier).toInt() else 0,
            critChance = if (type == ItemType.WEAPON) 0.05f else 0f,
            dodgeChance = if (type == ItemType.BOOTS) 0.05f else 0f
        )
    }
    
    private fun generateConsumable(id: Int, level: Int, rarity: String, pos: GridPosition?): Item {
        val potionTypes = listOf(
            "Health Potion" to "❤️",
            "Mana Potion" to "💙",
            "Elixir" to "🧪"
        )
        val (name, emoji) = potionTypes.random()
        return Item(id, name, ItemType.POTION, emoji, pos, healing = 30 + level * 5, value = 20, rarity = rarity)
    }
}
