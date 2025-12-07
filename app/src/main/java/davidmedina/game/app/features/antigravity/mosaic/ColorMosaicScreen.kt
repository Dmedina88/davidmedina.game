package davidmedina.game.app.features.antigravity.mosaic

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import davidmedina.game.app.ui.composables.gameBoxBackground
import org.koin.androidx.compose.koinViewModel

@Composable
fun ColorMosaicScreen() {
    val viewModel = koinViewModel<ColorMosaicViewModel>()
    val bitmap by viewModel.bitmapState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .gameBoxBackground()
            .onSizeChanged { size ->
                viewModel.init(size.width, size.height)
            }
    ) {
        bitmap?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Mosaic",
                contentScale = ContentScale.None,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            viewModel.fillZone(offset.x.toInt(), offset.y.toInt())
                        }
                    }
            )
        }

        Button(
            onClick = { viewModel.generateShapes() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text("Regenerate Shapes")
        }
    }
}
