package davidmedina.game.app.ui.screens

import android.content.pm.ActivityInfo
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.unit.dp
import davidmedina.game.app.R
import davidmedina.game.app.ui.composables.DMGCard
import davidmedina.game.app.ui.composables.LockScreenOrientation
import davidmedina.game.app.ui.theme.Pink80
import davidmedina.game.app.ui.theme.Purple40
import davidmedina.game.app.ui.theme.PurpleGrey80
import davidmedina.game.app.viewmodel.GameScreenViewModel
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Preview
@Composable
fun GameScreen(gameScreenViewModel: GameScreenViewModel = koinViewModel()) {
    Row(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)


        val state by gameScreenViewModel.uiState.collectAsState()


        if (!state.initalized) {
            gameScreenViewModel.gameStart()
        }

        Timber.i("State %s", state.toString())

        SideInfoBar()

        Column {
            //opponet
            Row(
                Modifier
                    .background(Color.Green)
                    .padding(16.dp)
            ) {
                //failt Spot
                Box(
                    Modifier
                        .background(Purple40)
                        .width(150.dp)
                        .height(200.dp)
                ) {

                }

                Box(
                    Modifier
                        .background(PurpleGrey80)
                        .width(150.dp)
                        .height(200.dp)
                ) {

                }
                Box(
                    Modifier
                        .background(Purple40)
                        .width(150.dp)
                        .height(200.dp)
                ) {

                }
                Box(
                    Modifier
                        .background(PurpleGrey80)
                        .width(150.dp)
                        .height(200.dp)
                ) {

                }
            }

            //player
            Row(
                Modifier
                    .background(Color.Yellow)
                    .padding(16.dp)
            ) {
                //failt Spot
                Box(
                    Modifier
                        .background(Purple40)
                        .width(150.dp)
                        .height(200.dp)
                ) {

                }

                Box(
                    Modifier
                        .background(PurpleGrey80)
                        .width(150.dp)
                        .height(200.dp)
                ) {

                }
                Box(
                    Modifier
                        .background(Purple40)
                        .width(150.dp)
                        .height(200.dp)
                ) {

                }
                Box(
                    Modifier
                        .background(PurpleGrey80)
                        .width(150.dp)
                        .height(200.dp)
                ) {

                }


            }



            Button(onClick = {
                Timber.i("Test deal clicked ")
                gameScreenViewModel.dealOnTurn()
            }, Modifier.background(Color.Red, CircleShape)) {
                Text(text = "Deal")
            }
            // fack deck list


            Row {
//hand


                LazyRow(Modifier.weight(1f)) {
                    items(items = state.player.hand) {
                        AnimatedVisibility(visible = it.faceUp,
                        ) {
                            DMGCard(it)
                        }

                    }

                }//deck
                Box() {
                    if (state.player.deck.size >1){
                        DMGCard(state.player.deck[0])
                    }
                  
                   Text(
                       text = state.player.deck.size.toString())
                    
                }
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