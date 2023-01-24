package davidmedina.game.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun MainMenuScreen(
    onNewGameClicked: () -> Unit,
    onLinksClicked: () -> Unit,
    onFeedBackClicked: () -> Unit,
    onArtGenClicked: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = { onNewGameClicked()}) {
            Text(text = "New Game")
        }

        Button(onClick = { onLinksClicked() }) {
            Text(text = "Links")
        }

        Button(onClick = { onFeedBackClicked() }) {
            Text(text = "FeedBack")
        }
        Button(onClick = { onArtGenClicked() }) {
            Text(text = "art gen")
        }
    }

}

