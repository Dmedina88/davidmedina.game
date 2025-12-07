package davidmedina.game.app.features.antigravity.handtracking

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.sqrt
import kotlin.random.Random

enum class GameState {
    IDLE,
    EXPLORING,
    IN_COMBAT,
    VICTORY,
    DEFEAT
}

data class Character(
    val hp: Int = 100,
    val maxHp: Int = 100,
    val mana: Int = 50,
    val maxMana: Int = 50,
    val position: Offset = Offset(500f, 500f),
    val level: Int = 1,
    val experience: Int = 0
)

data class Enemy(
    val id: Int,
    val name: String,
    val hp: Int,
    val maxHp: Int,
    val attack: Int,
    val position: Offset,
    val isAlive: Boolean = true,
    val type: EnemyType = EnemyType.GOBLIN
)

enum class EnemyType(val displayName: String, val emoji: String) {
    GOBLIN("Goblin", "👺"),
    SKELETON("Skeleton", "💀"),
    DRAGON("Dragon", "🐉"),
    SLIME("Slime", "🟢")
}

data class Loot(
    val id: Int,
    val name: String,
    val position: Offset,
    val isCollected: Boolean = false,
    val emoji: String = "💎"
)

data class CombatLog(
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPlayerAction: Boolean = true
)

data class HandQuestState(
    val gameState: GameState = GameState.IDLE,
    val character: Character = Character(),
    val enemies: List<Enemy> = emptyList(),
    val loot: List<Loot> = emptyList(),
    val combatLogs: List<CombatLog> = emptyList(),
    val currentEnemy: Enemy? = null,
    val gestureCombo: List<GestureType> = emptyList(),
    val score: Int = 0,
    val dungeonLevel: Int = 1
)

class HandQuestViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(HandQuestState())
    val state = _state.asStateFlow()
    
    private var handTrackingJob: Job? = null
    private var combatJob: Job? = null
    
    // Gesture combo system for special attacks
    private val maxComboLength = 3
    private var lastGesture: GestureType = GestureType.IDLE
    private var gestureHoldTime = 0L
    
    fun startGame() {
        _state.update {
            it.copy(
                gameState = GameState.EXPLORING,
                character = Character(),
                enemies = generateEnemies(3),
                loot = generateLoot(5),
                combatLogs = listOf(CombatLog("🎮 Hand Quest Started! Use gestures to explore.", isPlayerAction = true)),
                score = 0,
                dungeonLevel = 1
            )
        }
    }
    
    fun updateHandState(handState: HandState) {
        if (!handState.isDetected) return
        
        when (_state.value.gameState) {
            GameState.EXPLORING -> handleExploring(handState)
            GameState.IN_COMBAT -> handleCombat(handState)
            else -> {}
        }
        
        // Track gesture combos
        trackGestureCombo(handState.gesture)
    }
    
    private fun handleExploring(handState: HandState) {
        // Update character position based on hand position
        val currentState = _state.value
        val newCharPos = Offset(
            handState.position.x.coerceIn(50f, 1000f),
            handState.position.y.coerceIn(50f, 1800f)
        )
        
        _state.update { 
            it.copy(
                character = it.character.copy(position = newCharPos)
            )
        }
        
        // Check for enemy collision
        currentState.enemies.forEach { enemy ->
            if (enemy.isAlive && !isInCombat()) {
                val distance = calculateDistance(newCharPos, enemy.position)
                if (distance < 150f) {
                    enterCombat(enemy)
                }
            }
        }
        
        // Check for loot collection with pinch gesture
        if (handState.gesture == GestureType.PINCH) {
            currentState.loot.forEach { loot ->
                if (!loot.isCollected) {
                    val distance = calculateDistance(newCharPos, loot.position)
                    if (distance < 100f) {
                        collectLoot(loot)
                    }
                }
            }
        }
    }
    
    private fun handleCombat(handState: HandState) {
        val currentEnemy = _state.value.currentEnemy ?: return
        
        // Pinch gesture = Attack
        if (handState.gesture == GestureType.PINCH && lastGesture != GestureType.PINCH) {
            performAttack(currentEnemy)
        }
        
        // Open palm = Defend (restore mana)
        if (handState.gesture == GestureType.OPEN_PALM && lastGesture != GestureType.OPEN_PALM) {
            defend()
        }
        
        // Check for special combo attacks
        checkComboAttacks()
        
        lastGesture = handState.gesture
    }
    
    private fun trackGestureCombo(gesture: GestureType) {
        if (gesture == GestureType.IDLE) return
        
        val currentTime = System.currentTimeMillis()
        
        // Reset combo if gesture held too long (> 1 second)
        if (gesture == lastGesture) {
            if (currentTime - gestureHoldTime > 1000) {
                _state.update { it.copy(gestureCombo = emptyList()) }
            }
            return
        }
        
        gestureHoldTime = currentTime
        
        val newCombo = (_state.value.gestureCombo + gesture).takeLast(maxComboLength)
        _state.update { it.copy(gestureCombo = newCombo) }
        
        Timber.d("Gesture combo: $newCombo")
    }
    
    private fun checkComboAttacks() {
        val combo = _state.value.gestureCombo
        val currentEnemy = _state.value.currentEnemy ?: return
        
        // Combo: PINCH -> OPEN_PALM -> PINCH = Special Attack (30 damage)
        if (combo.size == 3 && 
            combo[0] == GestureType.PINCH && 
            combo[1] == GestureType.OPEN_PALM && 
            combo[2] == GestureType.PINCH) {
            
            performSpecialAttack(currentEnemy)
            _state.update { it.copy(gestureCombo = emptyList()) }
        }
    }
    
    private fun performAttack(enemy: Enemy) {
        val damage = Random.nextInt(10, 25)
        val newHp = (enemy.hp - damage).coerceAtLeast(0)
        
        updateEnemy(enemy.copy(hp = newHp, isAlive = newHp > 0))
        addCombatLog("⚔️ You attack ${enemy.name} for $damage damage!", true)
        
        if (newHp <= 0) {
            onEnemyDefeated(enemy)
        } else {
            // Enemy counter-attack after delay
            viewModelScope.launch {
                delay(1000)
                enemyAttack(enemy)
            }
        }
    }
    
    private fun performSpecialAttack(enemy: Enemy) {
        val manaCost = 20
        val currentChar = _state.value.character
        
        if (currentChar.mana < manaCost) {
            addCombatLog("❌ Not enough mana for special attack!", true)
            return
        }
        
        val damage = Random.nextInt(30, 50)
        val newHp = (enemy.hp - damage).coerceAtLeast(0)
        
        updateEnemy(enemy.copy(hp = newHp, isAlive = newHp > 0))
        _state.update { 
            it.copy(
                character = it.character.copy(mana = (currentChar.mana - manaCost).coerceAtLeast(0))
            )
        }
        
        addCombatLog("✨ SPECIAL ATTACK! You deal $damage damage to ${enemy.name}!", true)
        
        if (newHp <= 0) {
            onEnemyDefeated(enemy)
        }
    }
    
    private fun defend() {
        val currentChar = _state.value.character
        val manaRestore = 10
        
        _state.update { 
            it.copy(
                character = it.character.copy(
                    mana = (currentChar.mana + manaRestore).coerceAtMost(currentChar.maxMana)
                )
            )
        }
        
        addCombatLog("🛡️ You defend and restore $manaRestore mana.", true)
    }
    
    private fun enemyAttack(enemy: Enemy) {
        if (!enemy.isAlive || _state.value.gameState != GameState.IN_COMBAT) return
        
        val damage = Random.nextInt(5, enemy.attack)
        val currentChar = _state.value.character
        val newHp = (currentChar.hp - damage).coerceAtLeast(0)
        
        _state.update { 
            it.copy(
                character = it.character.copy(hp = newHp)
            )
        }
        
        addCombatLog("💥 ${enemy.name} attacks for $damage damage!", false)
        
        if (newHp <= 0) {
            gameOver()
        }
    }
    
    private fun onEnemyDefeated(enemy: Enemy) {
        val expGain = enemy.maxHp * 2
        val scoreGain = enemy.maxHp * 5
        
        _state.update { 
            it.copy(
                gameState = GameState.VICTORY,
                currentEnemy = null,
                character = it.character.copy(experience = it.character.experience + expGain),
                score = it.score + scoreGain
            )
        }
        
        addCombatLog("🎉 You defeated ${enemy.name}! +$expGain XP, +$scoreGain Score", true)
        
        // Return to exploring after 2 seconds
        viewModelScope.launch {
            delay(2000)
            _state.update { it.copy(gameState = GameState.EXPLORING) }
            
            // Check if all enemies defeated
            if (_state.value.enemies.all { !it.isAlive }) {
                nextLevel()
            }
        }
    }
    
    private fun enterCombat(enemy: Enemy) {
        _state.update { 
            it.copy(
                gameState = GameState.IN_COMBAT,
                currentEnemy = enemy,
                gestureCombo = emptyList()
            )
        }
        
        addCombatLog("⚠️ Combat started with ${enemy.name}!", true)
    }
    
    private fun collectLoot(loot: Loot) {
        val updatedLoot = _state.value.loot.map {
            if (it.id == loot.id) it.copy(isCollected = true) else it
        }
        
        val hpRestore = Random.nextInt(10, 30)
        val currentChar = _state.value.character
        
        _state.update { 
            it.copy(
                loot = updatedLoot,
                character = it.character.copy(
                    hp = (currentChar.hp + hpRestore).coerceAtMost(currentChar.maxHp)
                ),
                score = it.score + 50
            )
        }
        
        addCombatLog("💎 Collected ${loot.name}! +$hpRestore HP, +50 Score", true)
    }
    
    private fun nextLevel() {
        val newLevel = _state.value.dungeonLevel + 1
        
        _state.update { 
            it.copy(
                dungeonLevel = newLevel,
                enemies = generateEnemies(3 + newLevel),
                loot = generateLoot(5 + newLevel)
            )
        }
        
        addCombatLog("🎊 Dungeon Level $newLevel! More enemies await...", true)
    }
    
    private fun gameOver() {
        _state.update { 
            it.copy(
                gameState = GameState.DEFEAT,
                currentEnemy = null
            )
        }
        
        addCombatLog("💀 You have been defeated! Final Score: ${_state.value.score}", false)
    }
    
    private fun updateEnemy(updatedEnemy: Enemy) {
        _state.update { 
            it.copy(
                enemies = it.enemies.map { e -> 
                    if (e.id == updatedEnemy.id) updatedEnemy else e 
                },
                currentEnemy = if (it.currentEnemy?.id == updatedEnemy.id) updatedEnemy else it.currentEnemy
            )
        }
    }
    
    private fun addCombatLog(message: String, isPlayerAction: Boolean) {
        _state.update { 
            it.copy(
                combatLogs = (it.combatLogs + CombatLog(message, isPlayerAction = isPlayerAction)).takeLast(10)
            )
        }
    }
    
    private fun generateEnemies(count: Int): List<Enemy> {
        val types = EnemyType.values()
        return List(count) { index ->
            val type = types[Random.nextInt(types.size)]
            val baseHp = when (type) {
                EnemyType.SLIME -> 30
                EnemyType.GOBLIN -> 50
                EnemyType.SKELETON -> 70
                EnemyType.DRAGON -> 100
            }
            
            Enemy(
                id = index,
                name = type.displayName,
                hp = baseHp + (_state.value.dungeonLevel * 10),
                maxHp = baseHp + (_state.value.dungeonLevel * 10),
                attack = 15 + (_state.value.dungeonLevel * 5),
                position = Offset(
                    Random.nextFloat() * 900f + 50f,
                    Random.nextFloat() * 1600f + 100f
                ),
                type = type
            )
        }
    }
    
    private fun generateLoot(count: Int): List<Loot> {
        val lootNames = listOf("Health Potion", "Mana Crystal", "Gold Coin", "Magic Gem", "Treasure")
        val emojis = listOf("❤️", "💙", "💰", "💎", "🏆")
        
        return List(count) { index ->
            Loot(
                id = index,
                name = lootNames[Random.nextInt(lootNames.size)],
                position = Offset(
                    Random.nextFloat() * 900f + 50f,
                    Random.nextFloat() * 1600f + 100f
                ),
                emoji = emojis[Random.nextInt(emojis.size)]
            )
        }
    }
    
    private fun calculateDistance(p1: Offset, p2: Offset): Float {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return sqrt(dx * dx + dy * dy)
    }
    
    private fun isInCombat(): Boolean {
        return _state.value.gameState == GameState.IN_COMBAT
    }
}
