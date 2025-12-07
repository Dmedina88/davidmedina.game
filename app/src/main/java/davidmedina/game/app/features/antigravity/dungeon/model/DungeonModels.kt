package davidmedina.game.app.features.antigravity.dungeon.model

import kotlin.math.abs

// Grid-based dungeon system
data class GridPosition(val x: Int, val y: Int) {
    fun distanceTo(other: GridPosition): Int {
        return abs(x - other.x) + abs(y - other.y)
    }
}

enum class TileType {
    EMPTY, FLOOR, WALL, DOOR, STAIRS_DOWN, STAIRS_UP, HEALING_FOUNTAIN, SHOP, INN
}

data class DungeonTile(
    val position: GridPosition,
    val type: TileType,
    val isRevealed: Boolean = false,
    val isVisible: Boolean = false
)

enum class ItemType {
    WEAPON, ARMOR, HELMET, BOOTS, ACCESSORY, POTION, GOLD
}

data class Item(
    val id: Int,
    val name: String,
    val type: ItemType,
    val emoji: String,
    val position: GridPosition? = null,
    val rarity: String = "Common",
    val value: Int = 0,
    val attack: Int = 0,
    val defense: Int = 0,
    val magic: Int = 0,
    val healing: Int = 0,
    val critChance: Float = 0f,
    val dodgeChance: Float = 0f
)

data class Buff(
    val name: String,
    val description: String,
    val duration: Int, // Turns
    val attackBonus: Float = 0f,
    val defenseBonus: Float = 0f,
    val magicBonus: Float = 0f
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

enum class PlayerClass(val title: String, val description: String, val emoji: String) {
    NEON_LICH("Neon Lich", "Undead AI. High Magic, Low HP.", "👾"),
    FLESH_WEAVER("Flesh Weaver", "Biopunk Summoner. Minions of meat.", "🥩"),
    QUANTUM_GAMBLER("Quantum Gambler", "RNG Manipulator. High Luck.", "🎲"),
    CHRONO_KNIGHT("Chrono Knight", "Time Warrior. Rewinds damage.", "⏳"),
    VOID_STALKER("Void Stalker", "Shadow Assassin. High Burst.", "🥷"),
    PLASMA_PALADIN("Plasma Paladin", "Energy Tank. High Defense.", "🛡️")
}

// Helper to get ability details per class
fun PlayerClass.getAbilityInfo(): Pair<String, Int> {
    return when(this) {
        PlayerClass.NEON_LICH -> "Data Rot" to 15
        PlayerClass.FLESH_WEAVER -> "Corpse Explosion" to 20
        PlayerClass.QUANTUM_GAMBLER -> "Jackpot Roll" to 10
        PlayerClass.CHRONO_KNIGHT -> "Rewind" to 25
        PlayerClass.VOID_STALKER -> "Shadow Assassin" to 15
        PlayerClass.PLASMA_PALADIN -> "Holy Nova" to 20
    }
}

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
    val magic: Int = 5,
    val baseCrit: Float = 0.05f,
    val baseDodge: Float = 0.05f,
    val gold: Int = 0,
    val inventory: List<Item> = emptyList(),
    val equippedWeapon: Item? = null,
    val equippedArmor: Item? = null,
    val equippedHelmet: Item? = null,
    val equippedBoots: Item? = null,
    val equippedAccessory: Item? = null,
    val playerClass: PlayerClass = PlayerClass.NEON_LICH,
    val activeBuffs: List<Buff> = emptyList()
) {
    fun getTotalAttack(): Int {
        val base = attack + (equippedWeapon?.attack ?: 0) + (equippedAccessory?.attack ?: 0)
        val buffMult = 1f + activeBuffs.sumOf { it.attackBonus.toDouble() }.toFloat()
        return (base * buffMult).toInt()
    }
    
    fun getTotalDefense(): Int {
        val base = defense + (equippedArmor?.defense ?: 0) + (equippedHelmet?.defense ?: 0) + (equippedBoots?.defense ?: 0)
        val buffMult = 1f + activeBuffs.sumOf { it.defenseBonus.toDouble() }.toFloat()
        return (base * buffMult).toInt()
    }
    
    fun getTotalMagic(): Int {
        val base = magic + (equippedWeapon?.magic ?: 0) + (equippedAccessory?.magic ?: 0)
        val buffMult = 1f + activeBuffs.sumOf { it.magicBonus.toDouble() }.toFloat()
        return (base * buffMult).toInt()
    }
    
    fun getTotalCrit(): Float = baseCrit + (equippedWeapon?.critChance ?: 0f) + (equippedAccessory?.critChance ?: 0f)
    fun getTotalDodge(): Float = baseDodge + (equippedBoots?.dodgeChance ?: 0f) + (equippedAccessory?.dodgeChance ?: 0f)
}

enum class DungeonGameState {
    EXPLORING,
    IN_COMBAT,
    INVENTORY,
    GAME_OVER,
    VICTORY,
    LEVEL_UP,
    CLASS_SELECTION,
    SHOPPING,
    RESTING,
    DEBUG_MENU
}

data class ClassicDungeonState(
    val gameState: DungeonGameState = DungeonGameState.EXPLORING,
    val player: Player = Player(),
    val dungeonLevel: Int = 1,
    val tiles: List<DungeonTile> = emptyList(),
    val enemies: List<DungeonEnemy> = emptyList(),
    val items: List<Item> = emptyList(),
    val shopItems: List<Item> = emptyList(), 
    val currentEnemy: DungeonEnemy? = null,
    val messages: List<String> = listOf("Welcome to Classic Dungeon!"),
    val turnCount: Int = 0,
    val gridWidth: Int = 20,
    val gridHeight: Int = 20,
    val viewportSize: Int = 11, 
    val isAutoPlaying: Boolean = false,
    val gameSpeed: Float = 1.0f,
    val isControlsExpanded: Boolean = true,
    // Persistent Stats
    val victoryCount: Int = 0,
    val maxFloorReached: Int = 0,
    val totalEnemiesDefeated: Int = 0
)

enum class AbilityType {
    DAMAGE, HEAL, BUFF, SUMMON, TIME_WARP
}

enum class CombatAction {
    ATTACK, DEFEND, MAGIC, FLEE
}
