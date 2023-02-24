package davidmedina.game.app.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GradientProgressBar(
    progress: Float,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    borderRadius: Dp = 4.dp,
) {
    val gradientShader = Brush.linearGradient(
        colors = gradientColors,
        start = Offset.Zero,
        end = Offset.Infinite,
    )

    Canvas(
        modifier = modifier
            .border(3.dp, color = Color.Gray, shape = RoundedCornerShape(borderRadius))


    ) {
        val barWidth = size.width * progress

        drawRoundRect(
            brush = gradientShader,
            size = Size(barWidth, size.height),
            cornerRadius = CornerRadius(borderRadius.toPx()),
        )
    }
}
