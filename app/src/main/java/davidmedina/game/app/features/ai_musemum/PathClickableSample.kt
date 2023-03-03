package davidmedina.game.app.features.ai_musemum

import android.graphics.PathMeasure
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.pow
import kotlin.math.sqrt

@Preview
@Composable
fun PathClickableSample() {
    var isPathClicked by remember { mutableStateOf(false) }
    val path = Path().apply {
        moveTo(100f, 100f)
        lineTo(200f, 300f)
        lineTo(700f, 300f)
        //close()
    }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = { offset ->
                val tapThreshold = 10f // adjust as needed
                val tapX = offset.x
                val tapY = offset.y
                val pos = floatArrayOf(0f, 0f)
                val pathMeasure = PathMeasure(path.asAndroidPath(), false)
                val pathLength = pathMeasure.length
                var found = false

                for (i in 0 until pathLength.toInt()) {
                    pathMeasure.getPosTan(i.toFloat(), pos, null)
                    val distance = sqrt((pos[0] - tapX).pow(2) + (pos[1] - tapY).pow(2))
                    if (distance < tapThreshold) {
                        found = true
                        break
                    }
                }
//                if (!found) {
//                    val bounds = RectF()
//                    path.asAndroidPath().computeBounds(bounds, true)
//                    if (bounds.contains(tapX, tapY)) {
//                        found = true
//                    }
//                }

                isPathClicked = found
            })
        }) {
        drawPath(
            path = path,
            color = if (isPathClicked) Color.Red else Color.Blue,
            style = Stroke(5f)
        )
    }
}
