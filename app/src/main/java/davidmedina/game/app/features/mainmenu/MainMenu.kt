package davidmedina.game.app.features.mainmenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import davidmedina.game.app.R
import davidmedina.game.app.Routes
import davidmedina.game.app.features.ai_musemum.RandomShapeScreen2
import davidmedina.game.app.ui.composables.ShakingButton
import davidmedina.game.app.ui.composables.TDMButton
import davidmedina.game.app.ui.composables.noRippleClickable
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random


@Composable
fun MainMenuScreen(navController: NavHostController) {

    var shapeCount by remember { mutableStateOf(5) }

    RandomShapeScreen2(shapeCount, 10, { shapeCount = it }) {
        val vm = koinViewModel<MainMenuViewModel>()
        val scrollState = rememberScrollState()
        var hide by remember { mutableStateOf(false) }
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = if (hide) Arrangement.Top else Arrangement.Center,
            horizontalAlignment =  Alignment.CenterHorizontally
        ) {


            AnimatedVisibility(hide.not()) {
            Title(vm::onTitleClicked)
        }
            IconButton(onClick = { hide = hide.not() }) {
                Image(painter = painterResource(id = R.drawable.host_eye), contentDescription ="eye")
            }

            AnimatedVisibility(hide.not()) {
                Button(onClick = { navController.navigate(Routes.OPNEING_TEXT.name) }) {
                    Text(text = "Text Scroll Open")
                }
            }
            AnimatedVisibility(hide.not()) {
                Button(onClick = { navController.navigate(Routes.GAME.name) }) {
                    Text(text = "Card Game")
                }
            }


            AnimatedVisibility(hide.not()) {
                Button(onClick = { navController.navigate(Routes.GAME.name) }) {
                    Text(text = "Card Game")
                }
            }

            AnimatedVisibility(hide.not()) {
                SideButton { navController.navigate(Routes.LINKS.name) }
            }

            AnimatedVisibility(hide.not()) {
                Button(onClick = { navController.navigate(Routes.FEEDBACK.name) }) {
                    Text(text = "FeedBack")
                }
            }

            AnimatedVisibility(hide.not()) {
                TDMButton(text = "ProtoType") {
                    navController.navigate(Routes.PROTO_GEN.name)
                }
            }

            AnimatedVisibility(hide.not()) {
                Button(onClick = { navController.navigate(Routes.ART_GEN.name) }) {
                    Text(text = "Composed Art")
                }
            }

            AnimatedVisibility(hide.not()) {
                Button(onClick = { navController.navigate(Routes.STORY_MODE.name) }) {
                    Text(text = "Blue_Ogre_Story")
                }
            }

            AnimatedVisibility(hide.not()) {
                TDMButton(text = "Waiting For Godot Test") {
                    navController.navigate(Routes.STORY_TEST.name)
                }
            }

            AnimatedVisibility(hide.not()) {
                Button(onClick = { navController.navigate(Routes.RPG_CHARACTER.name) }) {
                    Text(text = "RPG Charicter View Stats")
                }
            }

            AnimatedVisibility(hide.not()) {
                ShakingButton(
                    "I asked AI to do this",
                    isShakingEnabled = sevenSecondsBoolean(),
                    onClick = { navController.navigate(Routes.AI_ART.name) })
            }

            AnimatedVisibility(hide.not()) {
                TDMButton(text = "RPG_BATTLE") {
                    navController.navigate(Routes.RPG.name)
                }
            }
            AnimatedVisibility(hide.not()) {
                TDMButton(text = "RPG_MAP") {
                    navController.navigate(Routes.RPG_MAP.name)
                }
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
        mutableStateOf(System.nanoTime().toString().last() == '2' && Random.nextInt(8) == 0)
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


@Composable
fun sevenSecondsBoolean(): Boolean {
    var value by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
            delay(7000)
            value = !value
    }

    return value
}