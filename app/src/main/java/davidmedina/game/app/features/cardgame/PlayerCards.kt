package davidmedina.game.app.features.cardgame

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import davidmedina.game.app.ui.composables.DMGCard
import davidmedina.game.app.ui.theme.playingCardSize

@Composable
fun PlayerCards(
    state: CardGame,
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