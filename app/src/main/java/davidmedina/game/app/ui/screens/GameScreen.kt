package davidmedina.game.app.ui.screens

import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.ui.composables.DMGCard
import davidmedina.game.app.ui.composables.GameField
import davidmedina.game.app.ui.composables.LockScreenOrientation
import davidmedina.game.app.ui.composables.SideInfoBar
import davidmedina.game.app.ui.theme.PurpleGrey80
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

        SideInfoBar(gameScreenViewModel::dealOnTurn)

        Box(Modifier.fillMaxSize()) {

            Column {
                GameField(state)

                Row {
//hand

                    LazyRow(Modifier.weight(1f)) {
                        itemsIndexed(items = state.player.hand) { index, item ->
                            AnimatedVisibility(
                                visible = item.faceUp,
                            ) {
                                DMGCard(item) { gameScreenViewModel.readyPlayCard(index) }
                            }

                        }
                    }//deck


                    Box(
                        Modifier
                            .background(PurpleGrey80)
                            .width(150.dp)
                            .height(200.dp)
                    ) {
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
