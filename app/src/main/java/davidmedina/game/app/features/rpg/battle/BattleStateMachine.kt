package davidmedina.game.app.features.rpg.battle

import androidx.compose.runtime.*
import davidmedina.game.app.data.models.Items
import davidmedina.game.app.features.rpg.*
import davidmedina.game.app.util.TickHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    val isValid get() =  index>-1
}


data class Action(var ability: Ability, var attacker: Battler, val defender: Battler)


class BattleStateMachine(private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {

    var playerCharacters = mutableStateListOf<BattleCharacter>()
        private set
    var enemyCharacters = mutableStateListOf<BattleCharacter>()
        private set
    var playerInventory = mutableStateListOf<Items>()
        private set
    var paused by mutableStateOf(false)
        private set

    private var Battler.battleInfo
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

    private val tickHandler = TickHandler(
        scope,
        tickInterval
    )


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
            BattleCharacter(
                it
            )
        }.toMutableStateList()
        enemyCharacters = enemy.map {
            BattleCharacter(
                it
            )
        }.toMutableStateList()
        playerInventory = items.toMutableStateList()


        onTick {
            if (!paused) {
                for (index in playerCharacters.indices) {
                    playerCharacters[index] = updateSpeed(playerCharacters[index])
                }
                for (index in enemyCharacters.indices) {
                    enemyCharacters[index] = updateSpeed(enemyCharacters[index])

                    // enemy logic
                    val target =
                        Battler.Player(
                            playerCharacters.indexOfFirst { it.characterStats.isAlive })
                    val attacker =
                        Battler.Enemy(
                            enemyCharacters.indexOfFirst { it.turns > 0 })

                    attack(attacker, target)
                }
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

    private fun attack(attacker: Battler, defender: Battler) {
        if (attacker.isValid && defender.isValid) {
            if (attacker.battleInfo.turns > 0) {
                defender.battleInfo = defender.battleInfo.copy(
                    characterStats = defender.battleInfo.characterStats.takeDamage(
                        attack.damageType, attacker.battleInfo.characterStats.performAttack(attack)
                    )
                )
                attacker.battleInfo =
                    attacker.battleInfo.copy(turns = attacker.battleInfo.turns - 1)
            }
        }
    }

    fun onAction(action: Action) {

    }


    fun onPause() {
        paused = true
    }


    fun onResume() {
        paused = false

    }

}
