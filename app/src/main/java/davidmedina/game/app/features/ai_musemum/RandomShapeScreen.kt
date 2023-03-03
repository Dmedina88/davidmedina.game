package davidmedina.game.app.features.ai_musemum

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Preview
@Composable
fun RandomShapeScreen() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        (0 until 5).forEach { i ->
            val path = Path().apply {
                moveTo(
                    Random.nextInt(size.width.toInt()).toFloat(),
                    Random.nextInt(size.height.toInt()).toFloat()
                )
                (0 until 3).forEach { j ->
                    if (Random.nextBoolean()) lineTo(
                        Random.nextInt(size.width.toInt()).toFloat(),
                        Random.nextInt(size.height.toInt()).toFloat()
                    )
                    else quadraticBezierTo(
                        Random.nextInt(size.width.toInt()).toFloat(),
                        Random.nextInt(size.height.toInt()).toFloat(),
                        Random.nextInt(size.width.toInt()).toFloat(),
                        Random.nextInt(size.height.toInt()).toFloat()
                    )
                }
                close()
            }
            drawPath(
                path,
                color = Random.nextColor(),
                alpha = 0.5f,
                style = if (i % 2 == 0) Stroke(
                    width = 10f,
                    join = StrokeJoin.Round,
                    cap = StrokeCap.Round
                ) else Fill
            )
        }
    }
}

@Composable
fun RandomShapeScreen2(
     numShapes: Int = 5,
    maxShapes: Int = 10,
    onNumShapesChanged: (Int) -> Unit = {},
    content: @Composable () -> Unit,
) {


    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                (0 until numShapes.coerceAtMost(maxShapes)).forEach { i ->
                    val path = Path().apply {
                        moveTo(
                            Random.nextInt(size.width.toInt()).toFloat(),
                            Random.nextInt(size.height.toInt()).toFloat()
                        )
                        (0 until 3).forEach { j ->
                            if (Random.nextBoolean()) lineTo(
                                Random.nextInt(size.width.toInt()).toFloat(),
                                Random.nextInt(size.height.toInt()).toFloat()
                            )
                            else quadraticBezierTo(
                                Random.nextInt(size.width.toInt()).toFloat(),
                                Random.nextInt(size.height.toInt()).toFloat(),
                                Random.nextInt(size.width.toInt()).toFloat(),
                                Random.nextInt(size.height.toInt()).toFloat()
                            )
                        }
                        close()
                    }
                    drawPath(
                        path,
                        color = Random.nextColor(),
                        alpha = 0.5f,
                        style = if (i % 2 == 0) Stroke(
                            width = 10f,
                            join = StrokeJoin.Round,
                            cap = StrokeCap.Round
                        ) else Fill
                    )
                }
            }
            content()
        }
        Row(Modifier.padding(16.dp)) {
            Text("Number of shapes: $numShapes")
            Slider(
                modifier = Modifier.background(color = Color.Transparent),
                value = numShapes.toFloat(),
                onValueChange = {
                    onNumShapesChanged(it.toInt())
                },
                valueRange = 1f..maxShapes.toFloat(),
                steps = maxShapes - 1,

                )
        }
    }
}


@Preview
@Composable
fun RandomShapeSreen3(
    onShapeCountChanged: (Int) -> Unit = {},
    content: @Composable () -> Unit = {}
) {

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
                modifier = Modifier.background(Color.Transparent),
                value = shapeCount.toFloat(),
                onValueChange = { value ->
                    shapeCount = value.toInt()
                    onShapeCountChanged(shapeCount)
                },
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
    content()
}

