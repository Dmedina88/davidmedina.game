package davidmedina.game.app.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import davidmedina.game.app.R
import davidmedina.game.app.ui.theme.PurpleGrey80
import davidmedina.game.app.viewmodel.GameState

@Composable
fun GameField(gameState : GameState){
    Column() {
        //oponite side

        Row() {


            LazyRow {
                items(items = gameState.oponente.field) {
                    AnimatedVisibility(visible = it != null) {
                        it?.let { DMGCard(it) }
                    }
                    if (it == null) {
                        Box(
                            Modifier
                                .background(PurpleGrey80)
                                .width(150.dp)
                                .height(200.dp)
                        )
                    }
                }

            }
            Box( Modifier
                .background(PurpleGrey80)
                .width(150.dp)
                .height(200.dp)) {
                Image(
                    painter = painterResource(id = R.mipmap.cavis),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillHeight
                )

                Text(text = "Hand ${gameState.oponente.hand.size}")
            }

            Box( Modifier
                .background(PurpleGrey80)
                .width(150.dp)
                .height(200.dp)) {
                Image(
                    painter = painterResource(id = R.mipmap.cavis),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillHeight
                )

                Text(text = "Deck ${gameState.oponente.deck.size}")
            }
        }

        // player side
        LazyRow {
            items(items = gameState.player.field) {
                AnimatedVisibility(visible = it != null) {
                    it?.let { DMGCard(it) }
                }
                if (it == null) {
                    Box(
                        Modifier
                            .background(PurpleGrey80)
                            .width(150.dp)
                            .height(200.dp)
                    )
                }
            }
        }
    }
}