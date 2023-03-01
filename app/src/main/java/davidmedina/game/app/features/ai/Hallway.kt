package davidmedina.game.app.features.ai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun Hallway() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Define the gradient colors and positions
        val gradientColors = listOf(
            Color.Gray,
            Color.DarkGray,
            Color.Black
        )


        // Draw the walls of the room
        drawRect(
            Brush.verticalGradient(
                colors = gradientColors,
                startY = 0f,
                endY = size.height,
                tileMode = TileMode.Clamp
            ), Offset(0f, 0f), size
        )

        // Draw the door
        val doorWidth = size.width / 4
        val doorHeight = size.height / 2
        val doorX = size.width / 2 - doorWidth / 2
        val doorY = size.height - doorHeight
        drawRect(Color.White, Offset(doorX, doorY), Size(doorWidth, doorHeight))

        // Add some details to the walls
        drawLine(Color.Green, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 4f)
        drawLine(
            Color.Green,
            Offset(size.width, 0f),
            Offset(size.width, size.height),
            strokeWidth = 4f
        )
        drawLine(
            Color.Green,
            Offset(size.width, size.height),
            Offset(0f, size.height),
            strokeWidth = 4f
        )
        drawLine(Color.Green, Offset(0f, size.height), Offset(0f, 0f), strokeWidth = 4f)
    }
}
