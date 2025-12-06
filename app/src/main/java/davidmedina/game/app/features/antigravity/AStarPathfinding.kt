package davidmedina.game.app.features.antigravity

import java.util.PriorityQueue
import kotlin.math.abs

/**
 * A* Pathfinding algorithm for intelligent navigation
 * No ML - just classic algorithm that always finds optimal path
 */
object AStarPathfinding {
    
    private data class Node(
        val position: GridPosition,
        val gCost: Int, // Distance from start
        val hCost: Int, // Estimated distance to goal (heuristic)
        val parent: Node? = null
    ) : Comparable<Node> {
        val fCost: Int get() = gCost + hCost
        
        override fun compareTo(other: Node): Int = fCost.compareTo(other.fCost)
    }
    
    /**
     * Find optimal path from start to goal
     * Returns list of directions to follow
     */
    fun findPath(
        start: GridPosition,
        goal: GridPosition,
        tiles: List<DungeonTile>,
        gridWidth: Int,
        gridHeight: Int
    ): List<GridPosition>? {
        
        val openSet = PriorityQueue<Node>()
        val closedSet = mutableSetOf<GridPosition>()
        
        val startNode = Node(
            position = start,
            gCost = 0,
            hCost = manhattanDistance(start, goal)
        )
        
        openSet.add(startNode)
        
        while (openSet.isNotEmpty()) {
            val current = openSet.poll() ?: break
            
            // Reached goal
            if (current.position == goal) {
                return reconstructPath(current)
            }
            
            closedSet.add(current.position)
            
            // Check all neighbors
            val neighbors = getNeighbors(current.position, tiles, gridWidth, gridHeight)
            
            for (neighborPos in neighbors) {
                if (neighborPos in closedSet) continue
                
                val gCost = current.gCost + 1
                val hCost = manhattanDistance(neighborPos, goal)
                
                val neighborNode = Node(
                    position = neighborPos,
                    gCost = gCost,
                    hCost = hCost,
                    parent = current
                )
                
                // Check if better path exists
                val existingNode = openSet.find { it.position == neighborPos }
                if (existingNode == null || gCost < existingNode.gCost) {
                    openSet.remove(existingNode)
                    openSet.add(neighborNode)
                }
            }
        }
        
        // No path found
        return null
    }
    
    /**
     * Convert node chain to list of direction steps
     */
    private fun reconstructPath(goal: Node): List<GridPosition> {
        val path = mutableListOf<GridPosition>()
        var current: Node? = goal
        
        while (current != null) {
            val parent = current.parent
            if (parent != null) {
                val direction = GridPosition(
                    current.position.x - parent.position.x,
                    current.position.y - parent.position.y
                )
                path.add(0, direction) // Add to front
            }
            current = parent
        }
        
        return path
    }
    
    /**
     * Get valid walkable neighbors
     */
    private fun getNeighbors(
        pos: GridPosition,
        tiles: List<DungeonTile>,
        gridWidth: Int,
        gridHeight: Int
    ): List<GridPosition> {
        val neighbors = mutableListOf<GridPosition>()
        
        val directions = listOf(
            GridPosition(0, -1),  // Up
            GridPosition(0, 1),   // Down
            GridPosition(-1, 0),  // Left
            GridPosition(1, 0)    // Right
        )
        
        for (dir in directions) {
            val newPos = GridPosition(pos.x + dir.x, pos.y + dir.y)
            
            // Check bounds
            if (newPos.x < 0 || newPos.x >= gridWidth || newPos.y < 0 || newPos.y >= gridHeight) {
                continue
            }
            
            // Check if walkable
            val tile = tiles.find { it.position == newPos }
            if (tile != null && tile.type != TileType.WALL) {
                neighbors.add(newPos)
            }
        }
        
        return neighbors
    }
    
    /**
     * Manhattan distance heuristic (perfect for grid movement)
     */
    private fun manhattanDistance(a: GridPosition, b: GridPosition): Int {
        return abs(a.x - b.x) + abs(a.y - b.y)
    }
}
