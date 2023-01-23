package davidmedina.game.app.ui.screens

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import davidmedina.game.app.ui.composables.GameField
import davidmedina.game.app.ui.composables.LockScreenOrientation
import davidmedina.game.app.ui.composables.PlayerCards
import davidmedina.game.app.ui.composables.SideInfoBar
import davidmedina.game.app.viewmodel.GameScreenViewModel
import davidmedina.game.app.viewmodel.GameState
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

        SideInfoBar(state, gameScreenViewModel::dealOnTurn)

        MainField(state, gameScreenViewModel)

    }
}

//todo move vm out and add created fn for user event
@Composable
private fun MainField(
    state: GameState,
    gameScreenViewModel: GameScreenViewModel
) {
    Box(Modifier.fillMaxSize()) {
        Column {
            GameField(state)
            PlayerCards(state, gameScreenViewModel)
        }
        state.actionState?.let {
            ActionOverlay(it, { index ->
                gameScreenViewModel.fieldSelected(index)
            }, gameScreenViewModel::cancelAction)
        }
    }
}
