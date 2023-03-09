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
    val agro: Int = 1
)

const val maxTurns = 3

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
    var source: Battler? = null,
    val target: Battler? = null
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

    val selectedCharacter get() =  currentPlayerAction?.source?.battleInfo

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
        val tickHandler = TickHandler(this.viewModelScope,800)
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

    private fun performAction(
        source: Battler,
        target: Battler?,
        action: Ability,
        delay: Long = 1500L,
        effect: (Character) -> Character
    ) {
        // Check if the source and target are valid, and that the source has turns left and enough willpower to use the ability
        if (source.isValid && target?.isValid == true && source.battleInfo.turns > 0 && source.battleInfo.characterStats.will.current >= action.cost) {
            // Deduct the ability cost from the source's willpower
            source.battleInfo.characterStats.will.current -= action.cost
            source.battleInfo = source.battleInfo.copy(abilityBeingUsed = action)

            viewModelScope.launch {
                delay(delay)
                source.battleInfo = source.battleInfo.copy(abilityBeingUsed = null)
            }

            // Apply the effect to the target and update their battle info
            val newTargetStats = target.battleInfo.characterStats.let(effect)
            target.battleInfo =
                target.battleInfo.copy(characterStats = newTargetStats, lastAbilityUsedOn = action)

            // Delay for a short time before removing the last ability used by the target
            viewModelScope.launch {
                delay(delay)
                target.battleInfo = target.battleInfo.copy(lastAbilityUsedOn = null)
            }

            // Deduct one turn from the source's turns
            source.battleInfo = source.battleInfo.copy(turns = source.battleInfo.turns - 1)
            currentPlayerAction = null

        }
    }

    private fun offensiveAction(
        source: Battler,
        target: Battler,
        action: Ability.Offensive
    ) {
        performAction(source, target, action) {
            it.takeDamage(
                action.damageType,
                source.battleInfo.characterStats.performAttack(action)
            )
        }
    }

    private fun healAction(
        source: Battler,
        target: Battler,
        action: Ability.Heal
    ) {
        performAction(
            source,
            target,
            action
        ) { it.heal(source.battleInfo.characterStats.performHeal(action)) }
    }

    private fun stealthAction(
        source: Battler,
        action: Ability.Stealth
    ) {
        // Decrement the source's agro value
        val newAgro = (source.battleInfo.agro / 2).coerceAtLeast(1)
        source.battleInfo = source.battleInfo.copy(agro = newAgro)
    }

    private fun tauntAction(
        source: Battler,
        target: Battler,
        action: Ability.Taunt
    ) {
        // Increment the target's agro value
        val newAgro = target.battleInfo.agro + action.agro
        target.battleInfo = target.battleInfo.copy(agro = newAgro, lastAbilityUsedOn = action)
        currentPlayerAction = null
        // Delay for a short time before removing the last ability used by the target
        viewModelScope.launch {
            delay(1500L)
            target.battleInfo = target.battleInfo.copy(lastAbilityUsedOn = null)
        }
    }

    private fun buffAction(
        source: Battler,
        target: Battler,
        buffAbility: Ability.Buff
    ) {
        performAction(source, target, buffAbility) { it.addStatusEffect(buffAbility.effect) }
    }


    private fun onAction(action: Action) {
        when (action.ability) {
            is Ability.Offensive ->
                offensiveAction(
                    action.source!!,
                    action.target!!,
                    action.ability as Ability.Offensive
                )
            is Ability.Heal ->
                healAction(
                    action.source!!,
                    action.target!!,
                    action.ability as Ability.Heal
                )
            is Ability.Stealth ->
                stealthAction(
                    action.source!!,
                    action.ability as Ability.Stealth
                )
            is Ability.Taunt ->
                tauntAction(
                    action.source!!,
                    action.target!!,
                    action.ability as Ability.Taunt
                )
            is Ability.Buff ->
                buffAction(
                    action.source!!,
                    action.target!!,
                    action.ability as Ability.Buff
                )
            null -> {}
        }
    }

    fun characterSelected(battleCharacter: Battler) {
        if (battleCharacter.battleInfo.characterStats.isAlive) {
            if (currentPlayerAction?.ability is Ability.Heal){
                currentPlayerAction = currentPlayerAction?.copy(target = battleCharacter)
                currentPlayerAction?.let {
                    onAction(it)
                }
            }else{
                currentPlayerAction = Action(source = battleCharacter)
            }
        }
    }

    fun onAbilitySelected(ability: Ability) {
        currentPlayerAction = currentPlayerAction?.copy(ability = ability)
        if (ability is Ability.Taunt){
            currentPlayerAction = currentPlayerAction?.copy(target = currentPlayerAction?.source)
            currentPlayerAction?.let {
                onAction(it)
            }
        }
    }

    fun enemySelected(target: Battler) {
        currentPlayerAction = currentPlayerAction?.copy(target = target)
        //
        currentPlayerAction?.let {
            onAction(it)
        }
    }

    fun systemPause(newPaused: Boolean) {
        paused = newPaused
    }
}

private fun List<BattleCharacter>.pickByAggro(): Int {
    val aliveCharacters = filter { it.characterStats.isAlive }
    if (aliveCharacters.isEmpty()) {
        return -1
    }
    val totalAggro = aliveCharacters.sumOf { it.agro }
    var randomNumber = Random.nextInt(totalAggro)
    for ((index, character) in aliveCharacters.withIndex()) {
        if (randomNumber < character.agro) {
            return indexOf(character)
        }
        randomNumber -= character.agro
    }
    return -1
}