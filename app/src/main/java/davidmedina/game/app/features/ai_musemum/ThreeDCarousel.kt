package davidmedina.game.app.features.ai_musemum

import androidx.compose.runtime.Composable
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import davidmedina.game.app.features.storygame.blueoger.level1.BlueOgerOpening
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random


@Preview
@Composable
fun AIUIWorkDDCarousel() {
    ThreeDCarousel(
        listOf(
            { Puzzle() },
            { PuzzleGame() },
            { Hallway() },
            { ShapeScreen() },
            { Hallway() },
            { Hallway()  },
            { ArtBox_AI() },
            { MapWithGeographicalFeatures() }
        )
    )
}
@Composable
fun ThreeDCarousel(items: List<@Composable () -> Unit>) {
    val density = LocalDensity.current
    val maxRotation = 75f

    var currentIndex by remember { mutableStateOf(0) }
    val angleState = remember { Animatable(0f) }
    val isRotating = angleState.isRunning

    val offsetState = remember { Animatable(0f) }
    val isOffsetting = offsetState.isRunning

    val itemWidth = with(density) { 240.dp.toPx() }
    val itemHeight = with(density) { 320.dp.toPx() }

    val itemOffsetX = with(density) { 32.dp.toPx() }
    val itemOffsetY = with(density) { 48.dp.toPx() }

    val itemSpacingX = with(density) { 16.dp.toPx() }
    val itemSpacingY = with(density) { 16.dp.toPx() }
 val scope = rememberCoroutineScope()
    val scrollListener = rememberScrollableState { delta ->
        if (!isRotating && !isOffsetting) {
            val indexDelta = delta.roundToInt()
            val newIndex = currentIndex - indexDelta
                .coerceAtLeast(-1 * (items.size - 1))
                .coerceAtMost(items.size - 1 - currentIndex)
            if (newIndex != currentIndex) {
                currentIndex = newIndex
                scope.launch {
                    offsetState.animateTo(0f, spring(stiffness = Spring.StiffnessLow))
                    angleState.animateTo(
                        currentIndex * -1f * maxRotation,
                        spring(stiffness = Spring.StiffnessLow)
                    )
                }
            }
        }
        delta
    }

    Column(
        Modifier
            .fillMaxSize()
            .scrollable(
                orientation = Orientation.Vertical,
                state = scrollListener
            )
            .background(Color(0xFFCCCCCC))
            .padding(top = 24.dp)
    ) {
        val offset = with(density) { itemWidth + itemSpacingX }
        val offsetMultiplier = remember { mutableStateOf(0f) }

        items.forEachIndexed { index, item ->
            Box(
                Modifier
                    .offset {
                        IntOffset(
                            x = ((index - currentIndex) * offset + offsetState.value).roundToInt(),
                            y = itemOffsetY.roundToInt() + (offsetMultiplier.value * itemSpacingY).roundToInt()
                        )
                    }
                    .transformable(
                        state = rememberTransformableState { zoomChange, _, _ ->
                            if (!isRotating && !isOffsetting) {
                                offsetMultiplier.value += zoomChange / 10
                            }
                            true
                        }
                    )
                    .graphicsLayer {
                        val rotation = maxRotation * offsetMultiplier.value.coerceIn(-1f, 1f)
                        rotationZ = angleState.value + rotation
                        alpha = (1f - abs(rotation / maxRotation)) * 0.5f + 0.5f
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                    }
                    .width(itemWidth.dp)
                    .height(itemHeight.dp)
                    .background(Random.nextColor())
            ) {
                item()
            }
        }
    }
}
