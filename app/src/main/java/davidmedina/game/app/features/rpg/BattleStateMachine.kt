package davidmedina.game.app.features.rpg

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import davidmedina.game.app.data.models.Items
import davidmedina.game.app.util.TickHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class BattleCharacter(
    val characterStats: CharacterStats,
    val turns: Int = 0,
    val speedBuilt: Float = 0F
)


sealed class RPGBattleState{

}
 data class BattleState(
    val playerCharacters: List<BattleCharacter> = emptyList(),
    val enemyCharacters: List<BattleCharacter> = emptyList(),
    val playerInventory: List<Items> = emptyList(),
    val paused : Boolean = false,
    )


const val maxTurns = 3
const val tickInterval = 250L


class BattleStateMachine(private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {


    private val _state = MutableStateFlow(BattleState())
    val state: StateFlow<BattleState> = _state

    private val tickHandler = TickHandler(scope, tickInterval)

    private fun onTick(work: () -> Unit) {
        scope.launch {
            tickHandler.tickFlow.collect {
                    work()
                }
        }
    }

    fun init(
        enemys: List<CharacterStats>,
        playerCharacter: List<CharacterStats>,
        items: List<Items>
    ) {
        _state.update {
            BattleState(
                playerCharacter.map { BattleCharacter(it) },
                enemys.map { BattleCharacter(it) },
                playerInventory = items
            )

        }

        onTick {
            if (!_state.value.paused) {
                _state.update { battleState ->
                    battleState.copy(
                        playerCharacters = battleState.playerCharacters
                            .filter { it.characterStats.isAlive }
                            .map { updateSpeed(it) },
                        enemyCharacters = battleState.enemyCharacters
                            .filter { it.characterStats.isAlive }
                            .map { updateSpeed(it) }
                    )

                }
            }
            //todo check enamy
        }
    }

    fun playerImput() {

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

     fun onPause() {
        _state.update {
            it.copy(paused = true)
        }
    }

     fun onResume() {
        _state.update {
            it.copy(paused = false)
        }
    }

}