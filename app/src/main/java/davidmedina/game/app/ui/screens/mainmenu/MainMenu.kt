package davidmedina.game.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import davidmedina.game.app.ui.composables.TDMButton
import davidmedina.game.app.ui.composables.noRippleClickable
import davidmedina.game.app.ui.screens.mainmenu.MainMenuViewModel
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun MainMenuScreen(
    onNewGameClicked: () -> Unit = {},
    onLinksClicked: () -> Unit = {},
    onFeedBackClicked: () -> Unit = {},
    onProtoGenClicked: () -> Unit = {},
    onArtGenClicked: () -> Unit = {},
    onStoryModeClicked: () -> Unit = {},

    ) {

    val vm = koinViewModel<MainMenuViewModel>()


    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Title(vm::onTitleClicked)




        Button(onClick = { onNewGameClicked() }) {
            Text(text = "New Game")
        }

        SideButton(onLinksClicked)


        Button(onClick = { onFeedBackClicked() }) {
            Text(text = "FeedBack")
        }
        TDMButton("ProtoType") { onProtoGenClicked() }

        Button(onClick = { onArtGenClicked() }) {
            Text(text = "Composed Art")
        }

        Button(onClick = { onStoryModeClicked() }) {
            Text(text = "Blue_Ogre_Story")
        }
        if (vm.uiState.debugMode) {

            Text(text = vm.uiState.gameState.toString())

        }
    }
}

@Composable
private fun Title(charClicked: (Char) -> Unit = { }) {

    Row() {
        Text(
            modifier = Modifier
                .padding(4.dp)
                .noRippleClickable {
                    charClicked('T')
                },
            text = "T.", fontSize = 34.sp
        )
        Text(
            modifier = Modifier
                .padding(4.dp)
                .noRippleClickable {
                    charClicked('D')

                },
            text = "D.", fontSize = 34.sp
        )
        Text(
            modifier = Modifier
                .padding(4.dp)
                .noRippleClickable {
                    charClicked('M')
                },
            text = "M.", fontSize = 34.sp
        )
        Text(
            modifier = Modifier
                .padding(4.dp)
                .noRippleClickable {
                    charClicked('A')
                },
            text = "A", fontSize = 34.sp
        )

    }
}


@Composable
private fun SideButton(onLinksClicked: () -> Unit) {
    val toSide by remember {
        mutableStateOf(System.nanoTime().toString().last() == '2')
    }
    val modifier = if (toSide) {
        Modifier.fillMaxWidth()
    } else {
        Modifier
    }

    Row(modifier = modifier) {
        Button(onClick = { onLinksClicked() }) {
            Text(text = "Links")
        }
    }
}

