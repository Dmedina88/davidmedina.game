package davidmedina.game.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController


@Composable
fun MainMenu(
    onNewGameClicked: () -> Unit,
    onLinksClicked: () -> Unit,
    onFeedBackClicked: () -> Unit
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
    }

}

