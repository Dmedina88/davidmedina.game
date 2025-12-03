package davidmedina.game.app.features.antigravity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import davidmedina.game.app.ui.composables.gameBoxBackground
import org.koin.androidx.compose.koinViewModel

@Composable
fun GravityPlaygroundScreen() {
    val viewModel = koinViewModel<GravityPlaygroundViewModel>()
    val gravityObjects by viewModel.gravityObjects.collectAsState()
    val isGravityReversed by viewModel.isGravityReversed.collectAsState()

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
        
        androidx.compose.material3.Button(
            onClick = { viewModel.toggleGravity() },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            androidx.compose.material3.Text(
                text = if (isGravityReversed) "Normal Gravity" else "Reverse Gravity"
            )
        }
    }
}
