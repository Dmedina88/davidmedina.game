package davidmedina.game.app.features.ai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
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
