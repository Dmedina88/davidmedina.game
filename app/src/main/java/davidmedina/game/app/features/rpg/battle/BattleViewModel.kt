package davidmedina.game.app.features.rpg.battle

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import davidmedina.game.app.data.repository.MetaGameRepository
import davidmedina.game.app.features.rpg.data.*
import davidmedina.game.app.features.rpg.data.ability.Ability
import davidmedina.game.app.features.rpg.data.ability.abilityList
import davidmedina.game.app.features.rpg.data.ability.attack
import davidmedina.game.app.util.TickHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

data class BattleCharacter(
    val characterStats: Character,
    val turns: Int = 0,
    val speedBuilt: Float = 0F,
    val lastAbilityUsedOn: Ability? = null,
    val abilityBeingUsed: Ability? = null,
    val aggro: Int = 1
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


class BattleStateMachine(private val metaGameRepository: MetaGameRepository) : ViewModel() {

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

    val selectedCharacter get() =  currentPlayerAction?.attacker?.battleInfo

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


    private fun onTick(work: suspend () -> Unit) {
        val tickHandler = TickHandler(this.viewModelScope, tickInterval)
        viewModelScope.launch {
            tickHandler.tickFlow.collect {
                work()
            }
        }
    }


    fun init(
        enemy: List<Character>,
        items: List<Items>
    ) {
        viewModelScope.launch {
            val metaGame = metaGameRepository.getGameState().first()
            playerCharacters = metaGame.rpgCharacter.mapIndexed { index , charicter ->
                BattleCharacter(charicter.copy( ability =  abilityList), turns = 3)
            }.toMutableStateList()
            enemyCharacters = enemy.map {
                BattleCharacter(it)
            }.toMutableStateList()
            playerInventory = items.toMutableStateList()

            battleStage = BattleStage.BattleInProgress
            onTick {
                if (!paused && battleStage == BattleStage.BattleInProgress) {
                    for (index in playerCharacters.indices) {
                        playerCharacters[index] =
                            updateSpeed(playerCharacters[index]) { autoAttack() }
                    }

                    for (index in enemyCharacters.indices) {
                        enemyCharacters[index] = updateSpeed(enemyCharacters[index])
                        enemyAi()
                    }
                    //check if lost

                    if (playerCharacters.all { !it.characterStats.isAlive }) {
                        battleStage = BattleStage.BattleLost
                    } else if (enemyCharacters.all { !it.characterStats.isAlive }) {
                        battleStage = BattleStage.BattleWon
                        metaGameRepository.updateParty(playerCharacters.map { it.characterStats })

                    }
                }
            }
        }
    }

    private fun enemyAi() {
        //get attacker
        val attacker =
            Battler.Enemy(
                enemyCharacters.indexOfFirst { it.turns > 0 })
        //get target
        if (attacker.isValid && attacker.battleInfo.characterStats.isAlive) {
            val target =
                Battler.Player(playerCharacters.pickByAggro())

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

    private fun offensiveAction(
        attacker: Battler, defender: Battler,
        action: Ability.Offensive
    ) {
        // Check if the attacker and defender are valid, and that the attacker has turns left and enough willpower to use the ability
        if (attacker.isValid && defender.isValid && attacker.battleInfo.turns > 0 && attacker.battleInfo.characterStats.will.current >= action.cost) {

            // Deduct the ability cost from the attacker's willpower
            attacker.battleInfo.characterStats.will.current -= action.cost
            attacker.battleInfo = attacker.battleInfo.copy(abilityBeingUsed = action)

            viewModelScope.launch {
                delay(750)
                attacker.battleInfo = attacker.battleInfo.copy(abilityBeingUsed = null)
            }
            // Deal damage to the defender and update their battle info
            val attackDamage = attacker.battleInfo.characterStats.performAttack(action)
            val newDefenderStats = defender.battleInfo.characterStats.takeDamage(action.damageType, attackDamage)
            defender.battleInfo = defender.battleInfo.copy(characterStats = newDefenderStats, lastAbilityUsedOn = action)

            // Delay for a short time before removing the last ability used by the defender
            viewModelScope.launch {
                delay(750)
                defender.battleInfo = defender.battleInfo.copy(lastAbilityUsedOn = null)
            }

            // If the defender is the selected character and is now dead, reset the current player action
            if (defender.battleInfo == selectedCharacter && selectedCharacter?.characterStats?.isAlive == false) {
                currentPlayerAction = null
            }

            // Deduct one turn from the attacker's turns
            attacker.battleInfo = attacker.battleInfo.copy(turns = attacker.battleInfo.turns - 1)
        }

    }

    private fun onAction(action: Action) {
        when (action.ability) {
            is Ability.Offensive ->
                offensiveAction(
                    action.attacker!!, action.defender!!,
                    action.ability!! as Ability.Offensive
                )
            null -> {}
            is Ability.Heal ->  Unit//TODO()
            is Ability.Stealth -> Unit//TODO()
            is Ability.Taunt -> Unit//TODO()
            is Ability.Buff -> Unit//TODO()
        }
    }


    fun characterSelected(battleCharacter: Battler) {
        if (battleCharacter.battleInfo.characterStats.isAlive) {
            currentPlayerAction = Action(attacker = battleCharacter)
        }
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

private fun List<BattleCharacter>.pickByAggro(): Int {
    val aliveCharacters = filter { it.characterStats.isAlive }
    if (aliveCharacters.isEmpty()) {
        return -1
    }
    val totalAggro = aliveCharacters.sumOf { it.aggro }
    var randomNumber = Random.nextInt(totalAggro)
    for ((index, character) in aliveCharacters.withIndex()) {
        if (randomNumber < character.aggro) {
            return indexOf(character)
        }
        randomNumber -= character.aggro
    }
    return -1
}