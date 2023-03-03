package davidmedina.game.app.features.ai_musemum

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import davidmedina.game.app.R
import davidmedina.game.app.ui.drawGrid
import kotlin.math.pow
import kotlin.math.sqrt

@Preview
@Composable
fun Map() {
    val mapWidth = 1000f
    val mapHeight = 1000f
    val landmarkPositions = listOf(
        Pair(0.2f, 0.3f), Pair(0.5f, 0.7f), Pair(0.8f, 0.1f), Pair(.99f, .99f), Pair(0f, 0f)
    )
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val aspectRatio = screenWidth / screenHeight
    val scaleFactor = if (aspectRatio > 1f) screenHeight / mapHeight else screenWidth / mapWidth
    val landmarkPositionsScaled = landmarkPositions.map {
        Pair(it.first * mapWidth * scaleFactor, it.second * mapHeight * scaleFactor)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.gen_background_6),
            contentDescription = "Map",
            modifier = Modifier.size(mapWidth * scaleFactor, mapHeight * scaleFactor)
        )
        for (position in landmarkPositionsScaled) {
            Image(
                painter = painterResource(R.drawable.gen_structure_10),
                contentDescription = "Landmark",
                modifier = Modifier.size(150 * scaleFactor).offset(position.first, position.second)
            )
        }
    }
    Box(modifier = Modifier.fillMaxSize().drawBehind { drawGrid(color = Color.LightGray, alpha = .5f) })
}


fun distance(p1: Pair<Float, Float>, p2: Pair<Float, Float>): Float {
    return sqrt((p2.first - p1.first).pow(2) + (p2.second - p1.second).pow(2))
}



