package davidmedina.game.app.features.antigravity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import davidmedina.game.app.ui.composables.gameBoxBackground
import org.koin.androidx.compose.koinViewModel

@Composable
fun AntiGravityScreen() {
    val viewModel = koinViewModel<AntiGravityViewModel>()
    val gravityObjects by viewModel.gravityObjects.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .gameBoxBackground()
            .onSizeChanged { size ->
                viewModel.updateScreenSize(size.width.toFloat(), size.height.toFloat())
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    viewModel.spawnObject(offset)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    viewModel.flingObject(change.position, dragAmount)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            gravityObjects.forEach { obj ->
                drawCircle(
                    color = obj.color,
                    radius = obj.radius,
                    center = obj.position
                )
            }
        }
    }
}
