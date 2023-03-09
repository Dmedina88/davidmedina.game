package davidmedina.game.app.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.R
import davidmedina.game.app.features.cardgame.data.CardAction
import davidmedina.game.app.features.cardgame.data.CardState
import davidmedina.game.app.features.cardgame.data.mockCardState
import davidmedina.game.app.ui.theme.OldPaper
import davidmedina.game.app.ui.theme.playingCardSize



@Preview
@Composable
fun DMGCard(cardState: CardState = mockCardState, onPlayAction: (() -> Unit)? = null) {
    Card(
        modifier = Modifier.playingCardSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OldPaper)
        ) {

            Crossfade(cardState.faceUp) {
                if (it) {
                    Column(Modifier.padding(8.dp)) {
                        // name and cost row
                        Row {
                            Text(

                                modifier = Modifier.weight(1f),
                                text = cardState.cardData.name
                            )
                            Text("Cost ${cardState.cardData.cost}")
                        }
                        Button(onClick = { onPlayAction?.invoke() }) {
                            Image(
                                painter = painterResource(cardState.cardData.imageId),
                                contentDescription = cardState.cardData.name
                            )
                        }


                        Box(
                            modifier = Modifier
                                .height(25.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.mipmap.heart),
                                contentDescription = "heart"
                            )
                            Text(
                                text = cardState.cardData.life.toString()
                            )
                        }


                        LazyColumn(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            items(cardState.cardData.actions) { action ->
                                when (action) {
                                    is CardAction.Attack -> Text(text = action.toString())
                                    is CardAction.DirectAttack -> Text(text = action.toString())
                                    is CardAction.Heal -> Text(text = action.toString())
                                }
                            }
                        }
                    }

                } else {
                    Image(
                        painter = painterResource(id = R.mipmap.cavis),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillHeight
                    )
                }
            }
        }
    }
}


