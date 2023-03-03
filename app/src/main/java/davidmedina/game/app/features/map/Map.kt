package davidmedina.game.app.features.map

import android.media.Image
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.R
import davidmedina.game.app.ui.composables.resizeWithCenterOffset
import davidmedina.game.app.ui.drawGrid
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.sqrt

//map class that has a @param landmarks, @param mapImage, @param size
class Map(
    val landmarks: List<Landmark>,
    val mapImage: Int,
    val size: Size
)

//landmark class that has a @param name, @param position, @param image
class Landmark(val name: String, val position: Offset, val image: Int)

//sample map
val sampleMap =
    Map(
        listOf(
            Landmark("Cavid", Offset(.5f, .4f), R.drawable.gen_structure_10),
            Landmark("Cavid", Offset(.35f, .75f), R.drawable.gen_structure_10),
            Landmark("Cavid", Offset(.25f, .15f), R.drawable.gen_structure_10),
            Landmark("Cavid", Offset(.75f, .25f), R.drawable.gen_structure_10),
            Landmark("Cavid", Offset(.65f, .65f), R.drawable.gen_structure_10),
            Landmark("Cavid", Offset(.85f, .85f), R.drawable.gen_structure_10),
            Landmark("Cavid", Offset(.15f, .85f), R.drawable.gen_structure_10),
        ), R.drawable.gen_background_9, Size(1000f, 1000f)
    )


//composable fn that takes in map gets the size of the device screen in order to scale the map
//and then draws the map and landmarks, use LocalConfiguration.current to calculate the scale factor

@Preview
@Composable
fun MapScreen(map: Map = sampleMap) {

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val aspectRatio = screenWidth / screenHeight
    val scaleFactor =
        if (aspectRatio > 1f) screenHeight / map.size.height else screenWidth / map.size.width

    var playerTargetOffset by remember { mutableStateOf<Offset?>(null) }

    var playerPosition by remember { mutableStateOf(map.landmarks.first().position) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(map.mapImage),
            contentDescription = "Map",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        for (landmark in map.landmarks) {

            Image(
                painter = painterResource(landmark.image),
                contentDescription = landmark.name,
                modifier = Modifier
                    .resizeWithCenterOffset(
                        50.dp, 50.dp,
                        x = (landmark.position.x * screenWidth).dp,
                        y = (landmark.position.y * screenHeight).dp
                    )
                    .clickable {
                        if (getClosestLandmarks(map.landmarks, playerPosition).contains(landmark)) {
                            playerTargetOffset = landmark.position
                        }

                    }
            )
            //text for landmark name
            Text(
                text = landmark.name,
                modifier = Modifier
                    .offset(
                        x = (landmark.position.x * screenWidth).dp - 25.dp,
                        y = (landmark.position.y * screenHeight).dp + 25.dp
                    )
                    .padding(5.dp),
                color = Color.Green
            )

        }
        //draw lines from landmarks to the closest landmarks
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGrid()
            for (landmark in map.landmarks) {
                val closestLandmarks = getClosestLandmarks(map.landmarks, landmark.position)
                for (closestLandmark in closestLandmarks) {
                    drawLine(
                        color = Color(0xFF000000),
                        start = Offset(
                            (landmark.position.x * screenWidth).dp.toPx(),
                            (landmark.position.y * screenHeight).dp.toPx()
                        ),
                        end = Offset(
                            (closestLandmark.position.x * screenWidth).dp.toPx(),
                            (closestLandmark.position.y * screenHeight).dp.toPx()
                        ),
                        strokeWidth = 5f
                    )
                }
            }
        }


        Image(
            painter = painterResource(R.drawable.blue_oger_portrite),
            contentDescription = "player",
            modifier = Modifier
                .resizeWithCenterOffset(
                    50.dp, 50.dp,
                    x = (playerPosition.x * screenWidth).dp,
                    y = (playerPosition.y * screenHeight).dp
                )
        )

        LaunchedEffect(playerTargetOffset) {
            if (playerTargetOffset != null) {
                var percentage = 0f
                while (percentage < 1f) {
                    percentage += 0.0005f
                    playerPosition = lerp(playerPosition, playerTargetOffset!!, percentage)
                    delay(10)
                }
                playerTargetOffset = null
            }
        }

    }
}

fun lerp(a: Offset, b: Offset, percentage: Float): Offset {
    return Offset(
        a.x + (b.x - a.x) * percentage,
        a.y + (b.y - a.y) * percentage
    )
}

fun getClosestLandmarks(landmarks: List<Landmark>, position: Offset): List<Landmark> {
    val closestLandmarks = mutableListOf<Landmark>()
    for (landmark in landmarks) {
        if (closestLandmarks.size < 3) {
            closestLandmarks.add(landmark)
        } else {
            for (i in closestLandmarks.indices) {
                if (distance(landmark.position, position) < distance(
                        closestLandmarks[i].position,
                        position
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
    return sqrt((a.x - b.x).toDouble().pow(2.0) + Math.pow((a.y - b.y).toDouble(), 2.0))
        .toFloat()
}