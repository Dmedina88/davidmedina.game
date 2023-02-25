package davidmedina.game.app.features.mainmenu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import davidmedina.game.app.Routes
import davidmedina.game.app.features.ai.RandomShapeScreen2
import davidmedina.game.app.ui.composables.TDMButton
import davidmedina.game.app.ui.composables.noRippleClickable
import org.koin.androidx.compose.koinViewModel


@Composable
fun MainMenuScreen(navController: NavHostController) {

    var shapeCount by remember { mutableStateOf(5) }

    RandomShapeScreen2(shapeCount, 10, {
        shapeCount = it
    }) {


        val vm = koinViewModel<MainMenuViewModel>()

        val scrollState = rememberScrollState()
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Title(vm::onTitleClicked)

            Button(onClick = { navController.navigate(Routes.GAME.name) }) {
                Text(text = "New Game")
            }

            SideButton { navController.navigate(Routes.LINKS.name) }

            Button(onClick = { navController.navigate(Routes.FEEDBACK.name) }) {
                Text(text = "FeedBack")
            }

            TDMButton(text = "ProtoType") {
                navController.navigate(Routes.PROTO_GEN.name)
            }

            Button(onClick = { navController.navigate(Routes.ART_GEN.name) }) {
                Text(text = "Composed Art")
            }

            Button(onClick = { navController.navigate(Routes.STORY_MODE.name) }) {
                Text(text = "Blue_Ogre_Story")
            }

            TDMButton(text = "Waiting For Godot Test") {
                navController.navigate(Routes.STORY_TEST.name)
            }

            Button(onClick = { navController.navigate(Routes.RPG_CHARACTER.name) }) {
                Text(text = "RPG Charicter View Stats")
            }

            Button(onClick = { navController.navigate(Routes.AI_ART.name) }) {
                Text(text = "I asked AI to do this")
            }

            TDMButton(text = "RPG") {
                navController.navigate(Routes.RPG.name)
            }

            if (vm.uiState.debugMode) {
                Text(text = vm.uiState.gameState.toString())
            }
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

