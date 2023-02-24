package davidmedina.game.app.features.ai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import kotlin.random.Random
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


// i had to correct the ai a few times
//there where regreations shen i tod it to put it all together for me
@Preview
@Composable
fun ShapeScreen_Ai_Assisted() {

    var shapeCount by remember { mutableStateOf(9) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Shape Count: $shapeCount")
            Slider(
                value = shapeCount.toFloat(),
                onValueChange = { value -> shapeCount = value.toInt() },
                valueRange = 1f..50f,
                steps = 49
            )
        }
    }
    val shapes = remember(shapeCount) {
        (0 until shapeCount).map {
            Shape(
                Random.nextColor(),
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextInt(50, 150)
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        shapes.forEach { shape ->
            drawShape(
                color = shape.color,
                x = shape.x * size.width,
                y = shape.y * size.height,
                alpha = shape.alpha,
                size = shape.size
            )
        }
    }
}

data class Shape(
    val color: Color,
    val x: Float,
    val y: Float,
    val alpha: Float,
    val size: Int
)


fun DrawScope.drawShape(
    color: Color,
    x: Float,
    y: Float,
    alpha: Float,
    size: Int
) {
    val shapeType = Random.nextInt(3)
    val shapeSize = size.toFloat()

    drawContext.canvas.withSave {
        drawContext.canvas.translate(x, y)
        drawContext.canvas.rotate(Random.nextFloat() * 360f)

        when (shapeType) {
            0 -> drawCircle(
                color = color.copy(alpha = alpha),
                center = Offset(0f, 0f),
                radius = shapeSize / 2f
            )
            1 -> drawRect(
                color = color.copy(alpha = alpha),
                topLeft = Offset(-shapeSize / 2f, -shapeSize / 2f),
                size = Size(shapeSize, shapeSize)
            )
            2 -> drawPath(
                color = color.copy(alpha = alpha),
                path = Path().apply {
                    moveTo(-shapeSize / 2f, -shapeSize / 2f)
                    lineTo(0f, shapeSize / 2f)
                    lineTo(shapeSize / 2f, -shapeSize / 2f)
                    close()
                }
            )
        }
    }
}
