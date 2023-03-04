package davidmedina.game.app.features.rpg.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.R
import davidmedina.game.app.features.rpg.battle.ui.RPGBattleScreen
import davidmedina.game.app.features.rpg.states.CharacterMenuScreen
import davidmedina.game.app.features.storygame.blueoger.level1.BlueOgerOpening
import davidmedina.game.app.ui.composables.resizeWithCenterOffset
import davidmedina.game.app.ui.drawGrid
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

//map class that has a @param landmarks, @param mapImage, @param size
class Map(
    val landmarks: List<Landmark>, val mapImage: Int, val size: Size
)

//landmark class that has a @param name, @param position, @param image
class Landmark(
    val name: String,
    val position: Offset,
    val image: Int,
    val locationId: LandMarkId = LandMarkId.Unknown
)

enum class LandMarkId(val id: Int) {
    Home(0),
    Cave(1),
    Village(2),
    City(3),
    GraveYard(4),
    OlsPlace(5),
    HeavensHill(6),
    ThePit(7),
    GreatForest(8),
    Unknown(9)
}

//sample map
val sampleMap = Map(
    listOf(
        Landmark("Home", Offset(.5f, .4f), R.drawable.gen_structure_10),
        Landmark("Cave", Offset(.35f, .75f), R.drawable.gen_structure_10),
        Landmark("Village", Offset(.25f, .15f), R.drawable.gen_structure_10),
        Landmark("City", Offset(.75f, .25f), R.drawable.gen_structure_10),
        Landmark("GraveYard", Offset(.65f, .65f), R.drawable.gen_structure_10),
        Landmark("Ols Place", Offset(.85f, .85f), R.drawable.gen_structure_10),
        Landmark("Heavens hill", Offset(.15f, .85f), R.drawable.gen_structure_10),
        Landmark("The pit", Offset(.15f, .15f), R.drawable.gen_structure_10),
        Landmark("great forest", Offset(.85f, .15f), R.drawable.gen_structure_10),
    ), R.drawable.gen_background_9, Size(1000f, 1000f)
)


@Preview
@Composable
fun OverWorldMap(map: Map = sampleMap) {

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp

    var playerTargetLandmark by remember { mutableStateOf<Landmark?>(null) }

    var playerPosition by remember { mutableStateOf(map.landmarks.first().position) }
    var isEncounterActive by remember { mutableStateOf(false) }
    var playerLocation by remember { mutableStateOf(map.landmarks.firstOrNull()) }
    var showCharacterMenu by remember { mutableStateOf(false) }

    var playerLocationId by remember { mutableStateOf<LandMarkId?>(null) }
    val inMenu = showCharacterMenu || isEncounterActive

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(map.mapImage),
            contentDescription = "Map",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        for (landmark in map.landmarks) {

            Image(painter = painterResource(landmark.image),
                contentDescription = landmark.name,
                modifier = Modifier
                    .resizeWithCenterOffset(
                        50.dp,
                        50.dp,
                        x = (landmark.position.x * screenWidth).dp,
                        y = (landmark.position.y * screenHeight).dp
                    )
                    .clickable {
                        if (getClosestLandmarks(map.landmarks, playerPosition).contains(landmark)) {
                            playerTargetLandmark = landmark
                            playerLocation = null
                        }

                    })
            //text for landmark name
            Text(
                text = landmark.name, modifier = Modifier
                    .offset(
                        x = (landmark.position.x * screenWidth).dp - 25.dp,
                        y = (landmark.position.y * screenHeight).dp + 25.dp
                    )
                    .padding(5.dp), color = Color.Green
            )

        }
        //draw lines from landmarks to the closest landmarks
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGrid()
            val closestLandmarks = getClosestLandmarks(map.landmarks, playerPosition)
            for (closestLandmark in closestLandmarks) {
                drawLine(
                    color = Color(0xFF000000), start = Offset(
                        (playerPosition.x * screenWidth).dp.toPx(),
                        (playerPosition.y * screenHeight).dp.toPx()
                    ), end = Offset(
                        (closestLandmark.position.x * screenWidth).dp.toPx(),
                        (closestLandmark.position.y * screenHeight).dp.toPx()
                    ), strokeWidth = 5f
                )
            }
        }
    }


    Image(
        painter = painterResource(R.drawable.blue_oger_portrite),
        contentDescription = "player",
        modifier = Modifier.resizeWithCenterOffset(
            50.dp,
            50.dp,
            x = (playerPosition.x * screenWidth).dp,
            y = (playerPosition.y * screenHeight).dp
        )
    )

    //use coroutine to move player to target landmark
    LaunchedEffect(playerTargetLandmark, isEncounterActive) {
        if (playerTargetLandmark != null && isEncounterActive.not()) {
            val target = playerTargetLandmark!!.position
            val distance = distance(playerPosition, target)
            val steps = 200 * distance

            val stepX = (target.x - playerPosition.x) / steps
            val stepY = (target.y - playerPosition.y) / steps
            for (i in 0 until steps.toInt()) {
                playerPosition = Offset(playerPosition.x + stepX, playerPosition.y + stepY)
                delay(100)
                if (Random.nextFloat() < .05f) {
                    isEncounterActive = true
                }
            }
            playerLocation = playerTargetLandmark!!
            playerPosition = playerTargetLandmark!!.position
            playerTargetLandmark = null

        }
    }



    AnimatedVisibility(visible = inMenu.not()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Button(onClick = { showCharacterMenu = true }) {
                Text(
                    text = "Character Menu",
                    modifier = Modifier.padding(5.dp),
                )
            }

            AnimatedVisibility(visible = playerLocation != null) {
                Button(onClick = { 
                playerLocationId = playerLocation?.locationId
                    
                }) {
                    Text(
                        text = "enter: ${playerLocation?.name}",
                        modifier = Modifier.padding(5.dp),
                    )
                }
            }
        }
    }
    AnimatedVisibility(visible = showCharacterMenu) {
        CharacterMenuScreen {
            showCharacterMenu = false
        }
    }

    AnimatedVisibility(visible = isEncounterActive) {
        if (isEncounterActive) {
            RPGBattleScreen {
                isEncounterActive = false
            }
        }
    }

    AnimatedVisibility(visible = playerLocationId != null) {
         BlueOgerOpening()

    }


}

fun getClosestLandmarks(landmarks: List<Landmark>, position: Offset): List<Landmark> {
    val closestLandmarks = mutableListOf<Landmark>()
    for (landmark in landmarks) {
        if (closestLandmarks.size <= 3) {
            closestLandmarks.add(landmark)
        } else {
            for (i in closestLandmarks.indices) {
                if (distance(landmark.position, position) < distance(
                        closestLandmarks[i].position, position
                    )
                ) {
                    closestLandmarks[i] = landmark
                    break
                }
            }
        }
    }
    return closestLandmarks
}

fun distance(a: Offset, b: Offset): Float {
    return sqrt((a.x - b.x).toDouble().pow(2.0) + Math.pow((a.y - b.y).toDouble(), 2.0)).toFloat()
}
