package davidmedina.game.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import davidmedina.game.app.ui.composables.TDMButton
import davidmedina.game.app.ui.screens.worldgen.BoxArtScreen
import davidmedina.game.app.ui.screens.worldgen.CanvisArtScreen


@Composable
fun MainMenuScreen(
    onNewGameClicked: () -> Unit,
    onLinksClicked: () -> Unit,
    onFeedBackClicked: () -> Unit,
    onProtoGenClicked: () -> Unit,
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
        TDMButton("ProtoType") { onProtoGenClicked() }


        Button(onClick = {onArtGenClicked()  }) {
            Text(text = "Composed Art")
        }
    }

}

