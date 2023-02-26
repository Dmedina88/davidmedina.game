package davidmedina.game.app.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Preview
@Composable
fun ShakingButton(
    text: String = "",
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isShakingEnabled: Boolean = true,
) {
    var shakeCount by remember { mutableStateOf(0) }
    val shakeAnim by animateFloatAsState(
        targetValue = if (isShakingEnabled && shakeCount < 6) (if (shakeCount % 2 == 0) 5f else -5f) else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    LaunchedEffect(shakeCount) {
        delay(500L)
        if (shakeCount < 6) {
            shakeCount += 1
        } else {
            shakeCount = 0
            delay(500L)
        }
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer(
                rotationZ = shakeAnim,
            )
            .padding(8.dp) // add padding to prevent text getting cut off
    ) {
        Text(text)
    }
}
