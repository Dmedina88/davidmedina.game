package davidmedina.game.app.features.ai_musemum

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview



@Preview
@Composable
fun MapWithGeographicalFeatures() {
    // Define the coordinates of the mountain range
    val mountainCoords = listOf(
        Offset(200f, 300f),
        Offset(250f, 250f),
        Offset(300f, 275f),
        Offset(350f, 200f),
        Offset(400f, 250f),
        Offset(450f, 225f),
        Offset(500f, 300f),
        Offset(525f, 275f),
        Offset(550f, 300f),
        Offset(600f, 275f),
        Offset(625f, 225f),
        Offset(650f, 250f),
        Offset(675f, 225f),
        Offset(700f, 275f),
        Offset(750f, 250f),
        Offset(750f, 300f))


    // Define the coordinates of the river
    val riverCoords = listOf(
        Offset(150f, 500f),
        Offset(250f, 400f),
        Offset(400f, 450f),
        Offset(500f, 500f),
        Offset(600f, 550f),
        Offset(700f, 500f),
        Offset(800f, 450f)
    )

    // Define the coordinates of the jungle
    val jungleCoords = listOf(
        Offset(400f, 700f),
        Offset(450f, 600f),
        Offset(500f, 650f),
        Offset(550f, 625f),
        Offset(600f, 700f),
        Offset(650f, 650f),
        Offset(700f, 675f),
        Offset(750f, 600f)
    )

    // Define the color of the mountain range
    val mountainColor = Color(0xFF888888)

    // Define the color of the river
    val riverColor = Color(0xFF42a5f5)

    // Define the color of the jungle
    val jungleColor = Color(0xFF4caf50)

    // Composable function that draws the map
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Draw the mountain range
        drawPath(
            path = Path().apply {
                moveTo(mountainCoords.first().x, mountainCoords.first().y)
                for (coord in mountainCoords) {
                    lineTo(coord.x, coord.y)
                }
            },
            color = mountainColor,
            style = Stroke(5f)
        )
        drawPath(
            path = Path().apply {
                moveTo(mountainCoords.first().x, mountainCoords.first().y)
                for (coord in mountainCoords) {
                    lineTo(coord.x, coord.y)
                }
            },
            color = Color.LightGray.copy(.5f),
            style = Fill
        )

        // Draw the river
        drawPath(
            path = Path().apply {
                moveTo(riverCoords.first().x, riverCoords.first().y)
                for (coord in riverCoords) {
                    lineTo(coord.x, coord.y)
                }
            },
            color = riverColor,
            style = Stroke(width = 50f)
        )

        // Draw the jungle
        drawPath(
            path = Path().apply {
                moveTo(jungleCoords.first().x, jungleCoords.first().y)
                for (coord in jungleCoords) {
                    lineTo(coord.x, coord.y)
                }
            },
            color = jungleColor,
            style = Fill
        )
    }
}

