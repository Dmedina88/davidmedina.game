package davidmedina.game.app.features.rpg.battle

import androidx.compose.runtime.*
import davidmedina.game.app.data.models.Items
import davidmedina.game.app.features.rpg.*
import davidmedina.game.app.util.TickHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

data class BattleCharacter(
    val characterStats: Character,
    val turns: Int = 0,
    val speedBuilt: Float = 0F
)

const val maxTurns = 3
const val tickInterval = 250L

sealed class Battler(open val index: Int) {
    data class Player(override val index: Int) : Battler(index)
    data class Enemy(override val index: Int) : Battler(index)
    val isValid get() = index > -1
}

//now that i added this I DO wish i out all my state into one state class for the screen
sealed class BattleStage {
    object BattleStart : BattleStage()
    object BattleLost : BattleStage()
    object BattleWon : BattleStage()
    object BattleInProgress : BattleStage()
}

data class Action(
    var ability: Ability? = null,
    var attacker: Battler? = null,
    val defender: Battler? = null
)


class BattleStateMachine(private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {

    var playerCharacters = mutableStateListOf<BattleCharacter>()
        private set
    var enemyCharacters = mutableStateListOf<BattleCharacter>()
        private set
    var playerInventory = mutableStateListOf<Items>()
        private set
    var paused by mutableStateOf(false)
        private set
    var currentPlayerAction by mutableStateOf<Action?>(null)
        private set

    var battleStage by mutableStateOf<BattleStage>(BattleStage.BattleStart)
        private set

    var Battler.battleInfo
        get() =
            when (this) {
                is Battler.Enemy -> enemyCharacters[this.index]
                is Battler.Player -> playerCharacters[this.index]
            }
        private set(value) {
            when (this) {
                is Battler.Enemy -> enemyCharacters[this.index] = value
                is Battler.Player -> playerCharacters[this.index] = value
            }
        }

    private val tickHandler = TickHandler(scope, tickInterval)


    private fun onTick(work: () -> Unit) {
        scope.launch {
            tickHandler.tickFlow.collect {
                work()
            }
        }
    }


    fun init(
        enemy: List<Character>,
        players: List<Character>,
        items: List<Items>
    ) {

        playerCharacters = players.map {
            BattleCharacter(it, turns = 3)
        }.toMutableStateList()
        enemyCharacters = enemy.map {
            BattleCharacter(it)
        }.toMutableStateList()
        playerInventory = items.toMutableStateList()

        battleStage = BattleStage.BattleInProgress
        onTick {
            if (!paused && battleStage == BattleStage.BattleInProgress) {
                for (index in playerCharacters.indices) {
                    playerCharacters[index] = updateSpeed(
                        playerCharacters[index]
                    ) { autoAttack() }
                }
                for (index in enemyCharacters.indices) {
                    enemyCharacters[index] = updateSpeed(enemyCharacters[index])
                    enimeyAi()
                }
                //check if lost

                if (playerCharacters.all { !it.characterStats.isAlive }) {
                    battleStage = BattleStage.BattleLost
                } else if (enemyCharacters.all { !it.characterStats.isAlive }) {
                    battleStage = BattleStage.BattleWon
                }


            }
        }

    }

    private fun enimeyAi() {
        //get attacker
        val attacker =
            Battler.Enemy(
                enemyCharacters.indexOfFirst { it.turns > 0 })
        //get target
        if (attacker.isValid && attacker.battleInfo.characterStats.isAlive) {
            val target =
                Battler.Player(
                    playerCharacters.indexOfFirst { it.characterStats.isAlive })

            //chose action
            val action = attack
            if (Random.nextInt(12) == 3)
                offensiveAction(attacker, target, action)
        }
    }

    private fun autoAttack() {
        //get attacker
        val attacker =
            Battler.Player(
                playerCharacters.indexOfFirst { it.turns == 3 })
        //get target
        if (attacker.isValid && attacker.battleInfo.characterStats.isAlive) {
            val target =
                Battler.Enemy(
                    enemyCharacters.indexOfFirst { it.characterStats.isAlive })

            //chose action
            val action = attack
            offensiveAction(attacker, target, action)
        }
    }


    private fun updateSpeed(
        battleCharacter: BattleCharacter,
        speedOverFlowCallBack: () -> Unit = {}
    ): BattleCharacter {
        return if (battleCharacter.characterStats.isAlive) {
            var newSpeedBuilt = battleCharacter.speedBuilt + battleCharacter.characterStats.speed
            var newTurns = battleCharacter.turns
            if (newSpeedBuilt > 1) {
                if (maxTurns >= newTurns + 1) {
                    newTurns += 1
                    newSpeedBuilt -= 1f
                } else {
                    speedOverFlowCallBack()
                    newSpeedBuilt -= 1f
                }
            }
            battleCharacter.copy(speedBuilt = newSpeedBuilt, turns = newTurns)
        } else {
            battleCharacter
        }
    }

    private fun <T : DamageType> offensiveAction(
        attacker: Battler, defender: Battler,
        action: Ability.Offensive<T>
    ) {
        if (attacker.isValid && defender.isValid && attacker.battleInfo.turns > 0) {

            defender.battleInfo = defender.battleInfo.copy(
                characterStats = defender.battleInfo.characterStats.takeDamage(
                    attack.damageType, attacker.battleInfo.characterStats.performAttack(action)
                )
            )
            attacker.battleInfo =
                attacker.battleInfo.copy(turns = attacker.battleInfo.turns - 1)

        }
    }

    fun onAction(action: Action) {
        when (action.ability) {
            is Ability.Offensive<*> ->
                offensiveAction(
                    action.attacker!!, action.defender!!,
                    action.ability!! as Ability.Offensive<*>
                )
            null -> {}
        }
    }


    fun characterSelected(battleCharacter: Battler) {
        currentPlayerAction = Action(attacker = battleCharacter)
    }

    fun onAbilitySelected(ability: Ability) {
        currentPlayerAction = currentPlayerAction?.copy(ability = ability)
    }

    fun targetSelected(target: Battler) {
        currentPlayerAction = currentPlayerAction?.copy(defender = target)
        //
        currentPlayerAction?.let {
            onAction(it)
        }
        currentPlayerAction = null
    }


    fun onPause() {
        paused = true
    }


    fun onResume() {
        paused = false

    }

}
