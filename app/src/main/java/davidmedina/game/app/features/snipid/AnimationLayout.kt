package davidmedina.game.app.features.snipid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class AnimationSpec(val value:String){
    SNAP("Snap"),SPRING("Spring"),TWEEN("Tween"), REPEATABLE("Repeat"),
    INFINITE_REPEATABLE("Infinite") }
@Preview
@Composable
fun AnimationLayout() {

    var state by remember { mutableStateOf(false) }

    var animSpec by remember { mutableStateOf("snap") }

    val startColor = Color.Blue

    val endColor = Color.Green

    val backgroundColor by animateColorAsState(
        if (state) endColor else startColor,
        getAnimationSpec(animSpec)
    )

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor),
        verticalArrangement = Arrangement.Center
    ) {
        AnimationButtons(onValueChanged = { currState, animName ->
            animSpec = animName
            state = currState
        }, state = state)
    }
}

@Composable
fun AnimationButtons(onValueChanged: (Boolean, String) -> Unit, state: Boolean) {
    for (anim in AnimationSpec.values()) {
        Button(
            onClick = { onValueChanged(!state, anim.value) },
            modifier = Modifier.height(50.dp).width(100.dp).padding(top = 10.dp),
            content = {
                Text(text = anim.value, color = Color.White)
            })
    }
}

fun getAnimationSpec(spec: String): androidx.compose.animation.core.AnimationSpec<Color> {

    return when (spec) {
        AnimationSpec.SPRING.value -> {
            spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessMedium
            )
        }
        AnimationSpec.TWEEN.value -> {
            tween(
                durationMillis = 2000,
                delayMillis = 500,
                easing = LinearOutSlowInEasing
            )
        }
        AnimationSpec.REPEATABLE.value -> {
            repeatable(
                iterations = 3,
                animation = tween(durationMillis = 200),
                repeatMode = RepeatMode.Reverse
            )
        }
        AnimationSpec.INFINITE_REPEATABLE.value -> {
            infiniteRepeatable(
                animation = tween(durationMillis = 200),
                repeatMode = RepeatMode.Reverse
            )
        }

        else -> {
            snap(delayMillis = 40)
        }

    }
}