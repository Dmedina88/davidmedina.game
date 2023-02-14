package davidmedina.game.app.features.rpg

import androidx.compose.runtime.*
import davidmedina.game.app.data.models.Items
import davidmedina.game.app.util.TickHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


data class BattleCharacter(
    val characterStats: CharacterStats,
    val turns: Int = 0,
    val speedBuilt: Float = 0F
)


const val maxTurns = 3
const val tickInterval = 250L

sealed class Battler(open val index: Int) {
    data class Player(override val index: Int) : Battler(index)
    data class Enemy(override val index: Int) : Battler(index)
    val isValid get() =  index>-1
}


sealed class Action() {
    data class Attack(var attacker: Battler, val defender: Battler) : Action()
}


class BattleStateMachine(private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {


    var playerCharacters = mutableStateListOf<BattleCharacter>()
        private set
    var enemyCharacters = mutableStateListOf<BattleCharacter>()
        private set
    var playerInventory = mutableStateListOf<Items>()
        private set
    var paused by mutableStateOf(false)
        private set


    private val tickHandler = TickHandler(scope, tickInterval)

    private fun onTick(work: () -> Unit) {
        scope.launch {
            tickHandler.tickFlow.collect {
                work()
            }
        }
    }

    private var Battler.characterStats
        get() =
            when (this) {
                is Battler.Enemy -> enemyCharacters[this.index]
                is Battler.Player -> playerCharacters[this.index]
            }
        set(value) {
            when (this) {
                is Battler.Enemy -> enemyCharacters[this.index] = value
                is Battler.Player -> playerCharacters[this.index] = value
            }
        }


    fun init(
        enemys: List<CharacterStats>,
        players: List<CharacterStats>,
        items: List<Items>
    ) {

        playerCharacters = players.map { BattleCharacter(it) }.toMutableStateList()
        enemyCharacters = enemys.map { BattleCharacter(it) }.toMutableStateList()
        playerInventory = items.toMutableStateList()


        onTick {
            if (!paused) {

                for (index in playerCharacters.indices) {
                    playerCharacters[index] = updateSpeed(playerCharacters[index])
                }
                for (index in enemyCharacters.indices) {
                    enemyCharacters[index] = updateSpeed(enemyCharacters[index])

                    // enemy attack
                    val target = Battler.Player(playerCharacters.indexOfFirst { it.characterStats.isAlive })
                    val attacker = Battler.Enemy(enemyCharacters.indexOfFirst { it.turns > 0 })
                    attack(Action.Attack(attacker, target))
                }


            }

        }

    }


    private fun attack(action: Action.Attack) {

        if (action.attacker.isValid && action.defender.isValid) {
            if (action.attacker.characterStats.turns > 0) {
                val newHp =
                    action.defender.characterStats.characterStats.hp.current - action.attacker.characterStats.characterStats.damage
                action.defender.characterStats =
                    action.defender.characterStats.copy(
                        characterStats = action.defender.characterStats.characterStats.copy(
                            hp = action.defender.characterStats.characterStats.hp.copy(current = newHp)
                        )
                    )


                action.attacker.characterStats =
                    action.attacker.characterStats.copy(turns = action.attacker.characterStats.turns - 1)
            }
        }

    }


    private fun updateSpeed(battleCharacter: BattleCharacter): BattleCharacter {
        var newSpeedBuilt = battleCharacter.speedBuilt + battleCharacter.characterStats.speed
        var newTurns = battleCharacter.turns
        if (newSpeedBuilt > 1) {
            if (maxTurns >= newTurns + 1) {
                newTurns += 1
                newSpeedBuilt -= 1f
            } else {
                // random Attack
                newSpeedBuilt -= 1f
            }
        }
        return battleCharacter.copy(speedBuilt = newSpeedBuilt, turns = newTurns)
    }


    fun onAction(action: Action) {
        when (action) {
            is Action.Attack -> {

            }
        }
    }


    fun onPause() {
        paused = true
    }


    fun onResume() {
        paused = false

    }

}
