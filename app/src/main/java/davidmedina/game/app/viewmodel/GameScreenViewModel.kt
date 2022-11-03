package davidmedina.game.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import davidmedina.game.app.ui.composables.CardState
import davidmedina.game.app.ui.composables.mockCardState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber


data class PlayerState(
    val life: Int = 20,
    val energy: Int = 0,
    val deck: List<CardState>,
    val hand: List<CardState> = emptyList(),
    val field: List<CardState?> = listOf(null, null, null, null),
    val junkYard: List<CardState> = emptyList()
)


data class GameState(
    val initalized: Boolean = false,
    val turn: Int,
    //to keep things simple for now ding one player at a time
    val player : PlayerState
)

fun mockGetDeck(): List<CardState> =
    buildList {
        for (i in 1..20) {
            add(CardState(faceUp = false, cardData = mockCardState.cardData.copy(cost = i)))
        }
    }


val startingState =
    GameState(
        false,
        turn = 0,
        player = PlayerState(20 , 0 , mockGetDeck())

    )

class GameScreenViewModel : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(startingState)

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<GameState> = _uiState

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
        Timber.i("Test gameStart fn ")
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

    fun play() {
        _uiState.update {
            it.copy(player = it.player.play())
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

fun PlayerState.play(): PlayerState {

    val hand = this.hand.toMutableList()
    val field = this.field.toMutableList()


    hand.removeFirstOrNull()?.let {
        field[field.indexOf(null)] = it
    }

    return this.copy(field = field, hand = hand)

}

fun PlayerState.flipHand(): PlayerState {

    return this.copy(hand = this.hand.toMutableList().map { it.copy(faceUp = true) })

}




