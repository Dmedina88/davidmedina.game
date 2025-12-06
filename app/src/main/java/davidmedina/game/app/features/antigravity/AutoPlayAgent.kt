package davidmedina.game.app.features.antigravity

import kotlin.random.Random

/**
 * IMPROVED Auto-play AI agent with smarter pathfinding and strategy
 * 
 * Key improvements:
 * - Prioritizes healing fountains when low HP
 * - Seeks stairs efficiently when level is cleared
 * - Better stuck detection and recovery
 * - Collects loot opportunistically
 * - Actually completes the game!
 */
class AutoPlayAgent {
    
    private var lastPlayerPosition: GridPosition? = null
    private var stuckCounter = 0
    private val maxStuckAttempts = 3
    private var lastStrategyChange = 0
    private var turnsSinceProgress = 0
    
    enum class ExplorationStrategy {
        SEEK_HEALING,      // Low HP - find fountain
        FIND_STAIRS,       // Clear level - descend
        HUNT_ENEMIES,      // Active combat
        COLLECT_LOOT,      // Grab nearby items
        EXPLORE_UNKNOWN    // Default exploration
    }
    
    /**
     * Decide next move during exploration
     */
    fun decideExplorationMove(state: ClassicDungeonState): GridPosition? {
        val playerPos = state.player.position
        
        // Check if stuck (same position)
        if (playerPos == lastPlayerPosition) {
            stuckCounter++
            turnsSinceProgress++
        } else {
            stuckCounter = 0
            turnsSinceProgress = 0
        }
        lastPlayerPosition = playerPos
        
        // If stuck for too long, try random direction
        if (stuckCounter >= maxStuckAttempts) {
            stuckCounter = 0
            return randomValidDirection(state)
        }
        
        // If no progress for many turns, force strategy change
        if (turnsSinceProgress > 20) {
            turnsSinceProgress = 0
            return randomValidDirection(state)
        }
        
        // Choose strategy based on game state
        val strategy = chooseStrategy(state)
        
        return when (strategy) {
            ExplorationStrategy.SEEK_HEALING -> moveTowardsHealingFountain(state)
            ExplorationStrategy.FIND_STAIRS -> moveTowardsStairs(state)
            ExplorationStrategy.HUNT_ENEMIES -> moveTowardsNearestEnemy(state)
            ExplorationStrategy.COLLECT_LOOT -> moveTowardsNearestLoot(state)
            ExplorationStrategy.EXPLORE_UNKNOWN -> moveTowardsUnexplored(state)
        }
    }
    
    /**
     * Decide action during combat
     */
    fun decideCombatAction(state: ClassicDungeonState): CombatAction {
        val player = state.player
        val enemy = state.currentEnemy ?: return CombatAction.ATTACK
        
        val hpPercent = player.hp.toFloat() / player.maxHp
        val manaPercent = player.mana.toFloat() / player.maxMana
        val enemyHpPercent = enemy.hp.toFloat() / enemy.maxHp
        
        // Smart combat decisions
        return when {
            // CRITICAL HP - flee unless boss

            
            // Low HP - defend more
            hpPercent < 0.35f && Random.nextFloat() < 0.5f -> CombatAction.DEFEND
            
            // Boss fight - use magic when available
            enemy.isBoss && player.mana >= 15 && Random.nextFloat() < 0.7f -> CombatAction.MAGIC
            
            // Strong enemy - use magic
            enemyHpPercent > 0.8f && manaPercent > 0.3f && player.mana >= 15 -> CombatAction.MAGIC
            
            // Finish with magic
            enemyHpPercent < 0.25f && player.mana >= 15 && Random.nextFloat() < 0.6f -> CombatAction.MAGIC
            
            // Default: attack
            else -> CombatAction.ATTACK
        }
    }
    
    /**
     * IMPROVED: Choose strategy with better priorities
     */
    private fun chooseStrategy(state: ClassicDungeonState): ExplorationStrategy {
        val player = state.player
        val hpPercent = player.hp.toFloat() / player.maxHp
        val aliveEnemies = state.enemies.count { it.isAlive }
        val visibleLoot = state.items.count { item ->
            item.position?.let { pos ->
                state.tiles.find { it.position == pos }?.isVisible == true
            } ?: false
        }
        val fountainVisible = state.tiles.any { it.type == TileType.HEALING_FOUNTAIN && it.isVisible }
        
        return when {
            // PRIORITY 1: Heal if critical HP and fountain visible
            hpPercent < 0.4f && fountainVisible -> ExplorationStrategy.SEEK_HEALING
            
            // PRIORITY 2: Find stairs if level is cleared
            aliveEnemies == 0 -> ExplorationStrategy.FIND_STAIRS
            
            // PRIORITY 3: Collect visible loot opportunistically
            visibleLoot > 0 && hpPercent > 0.5f && Random.nextFloat() < 0.3f -> ExplorationStrategy.COLLECT_LOOT
            
            // PRIORITY 4: Hunt enemies if healthy
            hpPercent > 0.6f && aliveEnemies > 0 -> ExplorationStrategy.HUNT_ENEMIES
            
            // PRIORITY 5: Explore to find more
            else -> ExplorationStrategy.EXPLORE_UNKNOWN
        }
    }
    
    /**
     * NEW: Find and move to healing fountain
     */
    private fun moveTowardsHealingFountain(state: ClassicDungeonState): GridPosition? {
        val playerPos = state.player.position
        val fountains = state.tiles.filter { 
            it.type == TileType.HEALING_FOUNTAIN && it.isVisible 
        }
        
        if (fountains.isEmpty()) {
            return moveTowardsUnexplored(state) // No fountain visible, explore
        }
        
        val nearest = fountains.minByOrNull { it.position.distanceTo(playerPos) }
        return nearest?.let { moveTowards(state, playerPos, it.position) }
    }
    
    /**
     * IMPROVED: Move towards stairs (end goal)
     */
    private fun moveTowardsStairs(state: ClassicDungeonState): GridPosition? {
        val playerPos = state.player.position
        val stairs = state.tiles.filter { 
            it.type == TileType.STAIRS_DOWN && it.isRevealed 
        }
        
        if (stairs.isEmpty()) {
            // No stairs found yet, explore to find them
            return moveTowardsUnexplored(state)
        }
        
        val nearest = stairs.minByOrNull { it.position.distanceTo(playerPos) }
        return nearest?.let { moveTowards(state, playerPos, it.position) }
    }
    
    /**
     * IMPROVED: Move towards nearest visible enemy
     */
    private fun moveTowardsNearestEnemy(state: ClassicDungeonState): GridPosition? {
        val playerPos = state.player.position
        val visibleEnemies = state.enemies.filter { 
            it.isAlive && state.tiles.find { t -> t.position == it.position }?.isVisible == true 
        }
        
        if (visibleEnemies.isEmpty()) {
            return moveTowardsUnexplored(state) // No enemies visible, explore
        }
        
        val nearest = visibleEnemies.minByOrNull { it.position.distanceTo(playerPos) }
        return nearest?.let { moveTowards(state, playerPos, it.position) }
    }
    
    /**
     * Move towards nearest visible loot
     */
    private fun moveTowardsNearestLoot(state: ClassicDungeonState): GridPosition? {
        val playerPos = state.player.position
        val visibleLoot = state.items.filter { item ->
            item.position?.let { pos ->
                state.tiles.find { it.position == pos }?.isVisible == true
            } ?: false
        }
        
        if (visibleLoot.isEmpty()) {
            return moveTowardsUnexplored(state)
        }
        
        val nearest = visibleLoot.minByOrNull { it.position!!.distanceTo(playerPos) }
        return nearest?.let { moveTowards(state, playerPos, it.position!!) }
    }
    
    /**
     * Move towards unexplored (unrevealed) tiles
     */
    private fun moveTowardsUnexplored(state: ClassicDungeonState): GridPosition? {
        val playerPos = state.player.position
        
        // Find closest unexplored floor tiles
        val unrevealedTiles = state.tiles.filter { 
            !it.isRevealed && it.type == TileType.FLOOR 
        }
        
        if (unrevealedTiles.isEmpty()) {
            // Everything explored, look for stairs or enemies
            val stairs = state.tiles.find { it.type == TileType.STAIRS_DOWN && it.isRevealed }
            if (stairs != null) {
                return moveTowards(state, playerPos, stairs.position)
            }
            
            // Just pick a random direction
            return randomValidDirection(state)
        }
        
        // Find nearest unrevealed tile
        val nearest = unrevealedTiles.minByOrNull { it.position.distanceTo(playerPos) }
        return nearest?.let { moveTowards(state, playerPos, it.position) }
    }
    
    /**
     * Calculate direction to move towards target using A* pathfinding
     */
    private fun moveTowards(
        state: ClassicDungeonState, 
        from: GridPosition, 
        to: GridPosition
    ): GridPosition? {
        // Use A* to find optimal path
        val path = AStarPathfinding.findPath(
            start = from,
            goal = to,
            tiles = state.tiles,
            gridWidth = state.gridWidth,
            gridHeight = state.gridHeight
        )
        
        // Return first step in path, or random if path fails
        return path?.firstOrNull() ?: randomValidDirection(state)
    }
    
    /**
     * Get random valid direction (fallback)
     */
    private fun randomValidDirection(state: ClassicDungeonState): GridPosition? {
        val playerPos = state.player.position
        val directions = listOf(
            GridPosition(0, -1),  // Up
            GridPosition(0, 1),   // Down
            GridPosition(-1, 0),  // Left
            GridPosition(1, 0)    // Right
        ).shuffled()
        
        for (dir in directions) {
            val newPos = GridPosition(playerPos.x + dir.x, playerPos.y + dir.y)
            val tile = state.tiles.find { it.position == newPos }
            
            if (tile != null && tile.type != TileType.WALL) {
                return dir
            }
        }
        
        return null
    }
    
    /**
     * Get performance metrics for learning
     */
    fun getPerformanceMetrics(state: ClassicDungeonState): AgentMetrics {
        return AgentMetrics(
            turnsPlayed = state.turnCount,
            enemiesDefeated = state.enemies.count { !it.isAlive },
            hpRemaining = state.player.hp,
            goldCollected = state.player.gold,
            dungeonLevel = state.dungeonLevel,
            playerLevel = state.player.level
        )
    }
}

// CombatAction moved to DungeonModels.kt

data class AgentMetrics(
    val turnsPlayed: Int,
    val enemiesDefeated: Int,
    val hpRemaining: Int,
    val goldCollected: Int,
    val dungeonLevel: Int,
    val playerLevel: Int
) {
    fun getScore(): Float {
        return (enemiesDefeated * 10f) + 
               (goldCollected * 0.5f) + 
               (dungeonLevel * 50f) + 
               (playerLevel * 25f) - 
               (turnsPlayed * 0.1f)
    }
}
