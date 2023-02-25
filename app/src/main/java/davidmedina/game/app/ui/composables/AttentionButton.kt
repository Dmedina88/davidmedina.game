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
    text: String = "TEEEESS AFD ",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var shake by remember { mutableStateOf(false) }
    val shakeAnim by animateFloatAsState(
        targetValue = if (shake) 10f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    LaunchedEffect(shake) {
        while (true) {
            delay(500L)
            shake = shake.not()
        }
    }
    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer(
                rotationZ = shakeAnim,
                translationX = if (shake) 10.dp.value else 0f,
                translationY = if (shake) (-10).dp.value else 0f
            )
            .padding(8.dp) // add padding to prevent text getting cut off
    ) {
        Text(text)
    }
}
