package davidmedina.game.app.viewmodel

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import davidmedina.game.app.ui.composables.CardState
import davidmedina.game.app.ui.composables.mockCardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber


data class PlayerState(val life: Int = 20, val energy: Int = 0, val cards: List<CardState>)


data class GameState(
    val turn: Int,
    val player1: PlayerState,
    val player2: PlayerState
)

fun mockGetDeck(): List<CardState> =
    buildList {
        for (i in 1..20) {
           add( CardState(faceUp = i % 2 ==0, cardData = mockCardState.cardData.copy(cost = i)))
        }
    }


val startingState =
    GameState(turn = 0, PlayerState(cards = mockGetDeck()), PlayerState(cards = mockGetDeck()))

class GameScreenViewModel : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(startingState)
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<GameState> = _uiState


    fun gameStart() {

        Timber.i("Test gameStart fn ")

//        for (i in 1..5){
                _uiState.value = _uiState.value.copy(
                    player1 = _uiState.value.player1.dealCard(),
                    player2 = _uiState.value.player2.dealCard())
//            }
    }



}


fun PlayerState.dealCard() : PlayerState {
    Timber.i("Test dealCard ")

    return this.copy(cards =this.cards.toMutableStateList().apply {
        try {

            val index  = indexOfFirst { !it.faceUp }
           this[index] =  this[index].copy(faceUp = true)
            Timber.i("try done "  + this[index].toString() )

        }catch (e: Throwable){
            Timber.i( "BROOOO" +this.joinToString { it.faceUp.toString() })
        }
    })

}



