package davidmedina.game.app.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import davidmedina.game.app.R
import davidmedina.game.app.features.cardgame.CardGameState
import davidmedina.game.app.ui.theme.PurpleGrey80
import davidmedina.game.app.ui.theme.playingCardSize

@Composable
fun GameField(gameState: CardGameState) {
    Column() {

        //spike check to see if using grid would be better for animations
        OpponiteField(gameState)
        // player side
        LazyRow {
            items(items = gameState.player.field) {
                AnimatedVisibility(visible = it != null) {
                    it?.let { DMGCard(it) }
                }
                if (it == null) {
                    Box(
                        Modifier
                            .playingCardSize()
                            .background(PurpleGrey80)
                    )
                }
            }
        }
    }
}

@Composable
private fun OpponiteField(gameState: CardGameState) {
    Row() {
        LazyRow {
            items(items = gameState.opponent.field) {
                AnimatedVisibility(visible = it != null) {
                    it?.let { DMGCard(it) }
                }
                if (it == null) {
                    Box(
                        Modifier
                            .playingCardSize()
                            .background(PurpleGrey80)

                    )
                }
            }

        }
        // opanite deck
        Box(
            Modifier
                .playingCardSize()

        ) {
            Image(
                painter = painterResource(id = R.mipmap.cavis),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillHeight
            )

            Text(
                modifier = Modifier.padding(8.dp),
                text = "Hand ${gameState.opponent.hand.size}"
            )
        }

        // openite hand
        Box(
            Modifier
                .playingCardSize()
                .background(PurpleGrey80)

        ) {
            Image(
                painter = painterResource(id = R.mipmap.cavis),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillHeight
            )

            Text(
                modifier = Modifier.padding(8.dp),
                text = "Deck ${gameState.opponent.deck.size}"
            )
        }
    }
}