package davidmedina.game.app.ui.screens

import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import davidmedina.game.app.R
import davidmedina.game.app.ui.composables.DMGCard
import davidmedina.game.app.ui.composables.GameField
import davidmedina.game.app.ui.composables.LockScreenOrientation
import davidmedina.game.app.ui.theme.Pink80
import davidmedina.game.app.viewmodel.GameScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun GameScreen(gameScreenViewModel: GameScreenViewModel = koinViewModel()) {
    Row(
        Modifier
            .fillMaxSize()
    ) {
        LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        val state by gameScreenViewModel.uiState.collectAsState()

        if (!state.initalized) {
            gameScreenViewModel.gameStart()
        }

        SideInfoBar()
        Box(Modifier.fillMaxSize()) {

            Column {
                GameField(state)

                Button(onClick = {
                    gameScreenViewModel.dealOnTurn()
                }, Modifier.background(Color.Red, CircleShape)) {
                    Text(text = "Deal")
                }

                Button(onClick = {

                    gameScreenViewModel.readyPlayCard(0)
                }, Modifier.background(Color.Red, CircleShape)) {
                    Text(text = "play")
                }
                // fack deck list


                Row {
//hand

                    LazyRow(Modifier.weight(1f)) {
                        items(items = state.player.hand) {
                            AnimatedVisibility(
                                visible = it.faceUp,
                            ) {
                                DMGCard(it)
                            }

                        }
                    }//deck
                    Box {
                        if (state.player.deck.size > 1) {
                            DMGCard(state.player.deck[0])
                        }

                        Text(
                            text = "Deck ${state.player.deck.size}"
                        )

                    }
                }
            }


            state.actionState?.let {
                ActionOverlay(it, { index->
                    gameScreenViewModel.fieldSelected(index)
                })
            }

        }
        }


}
//}

@Preview
@Composable
fun SideInfoBar(
    // turnClicked : () -> Unit
) {

    Column(
        Modifier
            .background(Pink80)
            .fillMaxHeight()
            .fillMaxWidth(.10f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(text = "Life")
        //enamy life
        Text(text = "20 /20")
        Image(
            painter = painterResource(R.drawable.ic_baseline_battery_0_bar_24),
            contentDescription = "energy icon"
        )

        Button(onClick = { /*TODO*/ }, Modifier.background(Color.Red, CircleShape)) {
            Text(text = "Turn")
        }


        Image(
            painter = painterResource(R.drawable.ic_baseline_battery_0_bar_24),
            contentDescription = "energy icon"
        )

        Text(text = "Life")
        // user life
        Text(text = "20 /20")

    }
}

