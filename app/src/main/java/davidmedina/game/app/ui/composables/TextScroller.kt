package davidmedina.game.app.ui.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp


@Preview
@Composable
fun TextScroller(text: String = opening) {
    val infiniteTransition = rememberInfiniteTransition()
    val scrollState = rememberScrollState()
    val scrollAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 10000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    Column(
        modifier = Modifier
            .background(Color.Black)
            .gameBoxBackground()
            .verticalScroll(scrollState)
    ) {

        Text(
            text = text,
            modifier = Modifier
                .offset { IntOffset(0, -scrollAnim.toInt()) },
            fontSize = 34.sp,
            lineHeight = 34.sp
        )

    }
}

private val opening = """ 
The blue ogre, named Grom, has lived in the village for many years, but he's always been an outcast due to his unusual color.
Despite being shunned by the other villagers, Grom has always tried to be helpful and kind to those around him.

However, one day, the village is attacked by a band of goblins who are seeking a magical artifact that is said to be hidden somewhere in the village. The villagers, including Grom, band together to fight off the goblins, but in the chaos of the battle, the artifact is stolen.

The villagers blame Grom for the theft, believing that he must have helped the goblins in some way due to his outsider status. Despite Grom's protestations of innocence, the villagers drive him out of the village, telling him never to return.

As Grom makes his way out of the village, he encounters a wise old sage who tells him that the artifact is actually a powerful talisman that can be used to protect the village from all harm. The sage also reveals that the goblins are working for a dark sorcerer who plans to use the talisman to unleash a powerful curse on the land.

Realizing that with the power of the amulate he would no longer need friends, Grom sets out on a quest to retrieve the talisman and and become to powerful to ever hurt again.
"""