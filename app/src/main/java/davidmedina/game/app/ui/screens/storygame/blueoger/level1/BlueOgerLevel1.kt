package davidmedina.game.app.ui.screens.storygame.blueoger.level1

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import davidmedina.game.app.R
import davidmedina.game.app.ui.composables.TDMTextBox
import davidmedina.game.app.ui.composables.noRippleClickable
import davidmedina.game.app.ui.composables.resizeWithCenterOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
@Preview
fun BlueOgerOpening() {


    val scope = rememberCoroutineScope()
    //anaiamtion

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
    ) {
        val screenHeight = this.maxHeight
        val screenWidth = this.maxWidth
        BackGround(screenHeight = screenHeight, screenWidth = screenWidth)
        Sky(screenHeight = screenHeight, screenWidth = screenWidth)
        Ground(screenHeight = screenHeight, screenWidth = screenWidth)
        Homes(screenHeight = screenHeight, screenWidth = screenWidth)
         Ogers(screenHeight = screenHeight, screenWidth = screenWidth)
        BlueOger(screenHeight = screenHeight, screenWidth = screenWidth)

        Column(Modifier.background(Color.Green)) {
            Text(text = " ${screenWidth.value} x ${screenHeight.value}", fontSize = 40.sp)
        }
    }

    var text by remember {
        mutableStateOf("")
    }
    var step by remember {
        mutableStateOf(1)
    }


    TDMTextBox(text, { step += 1 }, { step += 1 })

    LaunchedEffect(key1 = step, block = {
        scope.launch {
            delay(500)
            text = if (step == 1) {
                "My name is  David"
            } else {
                ""
            }
        }
    })

   

}


@Composable
private fun BackGround(screenHeight: Dp, screenWidth: Dp) {
    Image(
        painter = painterResource(id = R.drawable.gen_story_splats_5), null,
        Modifier
            .height(screenHeight)
            .width(screenWidth),
        contentScale = ContentScale.FillBounds
    )
}


@Composable
private fun Sky(screenHeight: Dp, screenWidth: Dp) {
    Image(
        painter = painterResource(id = R.drawable.gen_perp_sky_8), null,
        Modifier
            .height(screenHeight.div(2))
            .width(screenWidth)
            .offset(0.dp),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
private fun Ground(
    screenHeight: Dp, screenWidth: Dp
) {
    Image(
        painter = painterResource(id = R.drawable.gen_japan_ground_3), null,
        Modifier
            .height(screenHeight.div(2))
            .width(screenWidth)
            .offset(0.dp, screenHeight.div(2)),
        contentScale = ContentScale.FillBounds
    )
}


@Preview
@Composable
private fun Homes(
    screenWidth: Dp = 1000.dp,
    screenHeight: Dp = 800.dp,
) {
    val height =
        with(LocalDensity.current) {
            (this.density * 150f).toDp()
        }
    val width = 100.dp
    val x = screenWidth - width
    val y = screenHeight / 2

    Image(
        painter = painterResource(id = R.drawable.gen_structure_hut_4), null,
        Modifier.resizeWithCenterOffset(
            width,
            height,
            x - width,
            y
        ),
        contentScale = ContentScale.FillBounds
    )

    var treeState by remember { mutableStateOf(true) }


    Image(
        painter = painterResource(id = R.drawable.gen_structure_hut_4), null,
        Modifier
            .resizeWithCenterOffset(
                width,
                height,
                x - height,
                y + 34.dp
            )
            .noRippleClickable { treeState = treeState.not() }
        //.rotate(offsetAnimation)
        ,
        contentScale = ContentScale.FillBounds
    )

    Image(
        painter = painterResource(id = R.drawable.gen_structure_hut_4), null,
        Modifier
            .resizeWithCenterOffset(
                width,
                height,
                x,
                y + 34.dp
            ),
        contentScale = ContentScale.FillBounds,
    )

}


@Preview
@Composable
private fun Ogers(
    screenWidth: Dp = 1000.dp,
    screenHeight: Dp = 800.dp,
) {
    val height = 150.dp

    val width = 100.dp
    val x = screenWidth - width.times(2)
    val y = screenHeight / 2
    var danceState by remember { mutableStateOf(true) }

    val floatAnimation: Float by animateFloatAsState(
        if (danceState) -10f else 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val dpOffset: Dp by animateDpAsState(
        targetValue = if (danceState) 0.dp else 10.dp,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        )
    )
    LaunchedEffect(danceState) {
        danceState = danceState.not()
    }

    Image(
        painter = painterResource(id = R.drawable.other_oger), null,
        Modifier
            .background(Color.Blue)
            .resizeWithCenterOffset(
                width,
                height,
                x - height,
                y
            )
            .rotate(floatAnimation)
            .offset(dpOffset, dpOffset)
            .graphicsLayer {
                rotationY = 180F
            },
        contentScale = ContentScale.FillBounds
    )

    Image(
        painter = painterResource(id = R.drawable.other_oger), null,
        Modifier
            .background(Color.Yellow)

            .resizeWithCenterOffset(
                width,
                height,
                x + width,
                y + 34.dp
            )
            .rotate(floatAnimation)
            .offset(y = dpOffset)
            .graphicsLayer {
                rotationY = 180F
            },
        contentScale = ContentScale.FillBounds
    )

    Image(
        painter = painterResource(id = R.drawable.other_oger), null,
        Modifier
            .background(Color.Green)

            .resizeWithCenterOffset(
                width,
                height,
                x,
                y + 34.dp
            )
            .rotate(floatAnimation)
            .graphicsLayer {
                rotationY = 180F
            },
        contentScale = ContentScale.FillBounds,
    )

}


@Preview
@Composable
private fun BlueOger(
    screenWidth: Dp = 1000.dp,
    screenHeight: Dp = 800.dp,
) {
    val height = 175.dp
    val width = 115.dp

    var danceState by remember { mutableStateOf(true) }

    val floatAnimation: Float by animateFloatAsState(
        if (danceState) -10f else 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val dpOffset: Dp by animateDpAsState(
        targetValue = if (danceState) 0.dp else 10.dp,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        )
    )
    LaunchedEffect(danceState) {
        delay(1000)
        danceState = danceState.not()

    }

    Image(
        painter = painterResource(id = R.drawable.blue_oger), null,
        Modifier
            .resizeWithCenterOffset(
                width,
                height,
                screenWidth / 2.8f,
                screenHeight - width
            )
            .rotate(floatAnimation)
            .offset(dpOffset, dpOffset),
        contentScale = ContentScale.FillBounds
    )


}
