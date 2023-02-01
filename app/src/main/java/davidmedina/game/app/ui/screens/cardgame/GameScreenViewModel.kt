package davidmedina.game.app.ui.screens.cardgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import davidmedina.game.app.data.models.CardAction
import davidmedina.game.app.ui.composables.CardState
import davidmedina.game.app.ui.composables.mockCardState
import davidmedina.game.app.ui.screens.cardgame.ActionComposerState.PlayCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class PlayerState(
    val life: Int = 20,
    val energy: Int = 0,
    val deck: List<CardState>,
    val hand: List<CardState> = emptyList(),
    val field: List<CardState?> = listOf(null, mockCardState, null, null),
    val junkYard: List<CardState> = emptyList()
)


data class CardGameState(
    val initalized: Boolean = false,
    val turn: Int,
    val player: PlayerState,
    val opponent: PlayerState,
    val actionState: ActionComposerState?
)

fun mockGetDeck(): List<CardState> =
    buildList {
        for (i in 1..20) {
            add(CardState(faceUp = false, cardData = mockCardState.cardData.copy(cost = i)))
        }
    }


val startingState =
    CardGameState(
        false,
        turn = 0,
        player = PlayerState(20, 0, mockGetDeck()),
        opponent = PlayerState(20, 0, mockGetDeck()),
        actionState = null
    )

class GameScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(startingState)
    val uiState: StateFlow<CardGameState> = _uiState

    fun gameStart() {

        if (!_uiState.value.initalized) {
            for (i in 1..2) {
                _uiState.update {
                    it.copy(
                        initalized = true,
                        turn = 1,
                        player = _uiState.value.player.dealCard()
                    )
                }

                _uiState.update {
                    it.copy(
                        player = _uiState.value.player.flipHand()
                    )
                }
            }
        }
    }

    fun cancelAction() {
        _uiState.update {
            it.copy(actionState = null)
        }
    }

    fun dealOnTurn() {
        _uiState.update {
            it.copy(player = it.player.dealCard())
        }
        viewModelScope.launch {
            delay(200)
            _uiState.update {
                it.copy(player = it.player.flipHand())
            }
        }
    }

    fun readyPlayCard(cardToPlay: Int) {
        if (_uiState.value.player.hand.isNotEmpty())
            _uiState.update { currentState ->
                val valid = currentState.player.field.map { it == null }
                val oponente = currentState.opponent.field.map { false }

                currentState.copy(actionState = PlayCard(cardToPlay, null, valid, oponente))
            }
    }

    fun fieldSelected(index: Int) {
        when (val action = _uiState.value.actionState) {
            is PlayCard -> playCard(uiState.value, action, index) //todo maybe depect the deal?
            is ActionComposerState.AttackAction -> TODO()
            else -> {}
        }
    }

    private fun playCard(state: CardGameState, actionState: PlayCard, index: Int) {
        _uiState.update {
            state.copy(
                actionState = null,
                player = state.player.readyPlayCard(actionState.targetCardIndex, index)
            )
        }
    }
}


fun PlayerState.readyPlayCard(cardIndex: Int, targetIndex: Int): PlayerState {

    return when (field.indexOf(null)) {
        in 0..4 -> {
            val hand = this.hand.toMutableList()
            val field = this.field.toMutableList()
            hand.removeAt(cardIndex).let {
                field[targetIndex] = it
            }
            this.copy(field = field, hand = hand)

        }
        else -> {
            this
        }
    }

}

//fun GameState.updatePlayer((player: PlayerState))

fun PlayerState.dealCard(): PlayerState {

    val deck = this.deck.toMutableList()
    val hand = this.hand.toMutableList()

    deck.removeFirstOrNull()?.let {
        hand.add(it)
    }

    return this.copy(deck = deck, hand = hand)

}


fun PlayerState.flipHand(): PlayerState {

    return this.copy(hand = this.hand.toMutableList().map { it.copy(faceUp = true) })

}

sealed class ActionComposerState {

    data class PlayCard(
        val targetCardIndex: Int,
        val field: Int?,
        val validFields: List<Boolean>,
        val validOponenteFields: List<Boolean>
    ) : ActionComposerState()

    //PLAYER_INDEX for player card positon for card
    data class AttackAction(val Action: CardAction.Attack, val target: Int?) : ActionComposerState()

}




