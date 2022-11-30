package davidmedina.game.app.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import davidmedina.game.app.ui.theme.playingCardSize
import davidmedina.game.app.viewmodel.GameScreenViewModel
import davidmedina.game.app.viewmodel.GameState

@Composable
fun PlayerCards(
    state: GameState,
    gameScreenViewModel: GameScreenViewModel
) {
    Row {
        LazyRow(Modifier.weight(1f)) {
            itemsIndexed(items = state.player.hand) { index, item ->
                AnimatedVisibility(
                    visible = item.faceUp,
                ) {
                    DMGCard(item) { gameScreenViewModel.readyPlayCard(index) }
                }

            }
        }//deck

        Box(Modifier.playingCardSize()) {
            if (state.player.deck.size > 1) {
                DMGCard(state.player.deck[0])
            }

            Text(
                text = "Deck ${state.player.deck.size}"
            )

        }
    }
}