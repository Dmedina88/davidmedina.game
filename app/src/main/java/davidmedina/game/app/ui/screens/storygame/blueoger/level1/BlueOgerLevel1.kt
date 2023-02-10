package davidmedina.game.app.ui.screens.storygame.blueoger.level1

import android.content.pm.ActivityInfo
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
import davidmedina.game.app.data.models.clouds
import davidmedina.game.app.ui.composables.LockScreenOrientation
import davidmedina.game.app.ui.composables.TDMTextBox
import davidmedina.game.app.ui.composables.noRippleClickable
import davidmedina.game.app.ui.composables.resizeWithCenterOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


val sourceX = 1280.dp
val sourceY = 820.dp

data class ScreenInfo(val screenWidth: Dp,val screenHeight: Dp,val xScale: Float, val yScale: Float)

@Composable
@Preview
fun BlueOgerOpening() {

    val scope = rememberCoroutineScope()
    //anaiamtion
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
    ) {
        val screenHeight = this.maxHeight
        val screenWidth = this.maxWidth

        val screenInfo = ScreenInfo(
            this.maxWidth,
            this.maxHeight,
            this.maxWidth / sourceX,
            this.maxHeight / sourceY
        )

        BackGround(screenHeight = screenHeight, screenWidth = screenWidth)
        Sky(screenHeight = screenHeight, screenWidth = screenWidth)
        repeat(8) {
            Clouds(screenInfo)
        }

        Ground(screenHeight = screenHeight, screenWidth = screenWidth)
        Homes(screenHeight = screenHeight, screenWidth = screenWidth)
        Ogers(screenInfo)
        BlueOger(screenInfo)

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
fun Clouds(screenInfo: ScreenInfo) {
    val clouds = remember {
        clouds.random()
    }

    val startingX = remember { Random.nextInt(screenInfo.screenWidth.value.toInt()).dp }
    val startingY = remember { Random.nextInt(screenInfo.screenHeight.value.div(3).toInt()).dp }

    val height = 75.dp * screenInfo.yScale
    val width = 200.dp * screenInfo.yScale

    var cloudToggle by remember {
        mutableStateOf(false)
    }
    val dpOffset: Dp by animateDpAsState(
        targetValue = if (cloudToggle) -5.dp else 5.dp,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect("Unit") {
        cloudToggle = cloudToggle.not()
    }


    Text(
        text = dpOffset.toString(),
        modifier = Modifier
            .padding(100.dp, 100.dp)
            .background(Color.Yellow)
    )
    Image(
        painter = painterResource(id = clouds), null,
        Modifier
            .resizeWithCenterOffset(
                width,
                height,
                startingX,
                startingY + dpOffset
            )
            .graphicsLayer {
                rotationY = dpOffset.value
            },
        contentScale = ContentScale.FillBounds
    )
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
    val y = screenHeight / 1.6f

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
                width, height, x - height, y + 34.dp
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


@Composable
private fun Ogers(
 screenInfo: ScreenInfo
) {
    val width = 100.dp * screenInfo.yScale
    val height = 150.dp * screenInfo.yScale

    val x =  screenInfo.screenWidth - screenInfo.screenWidth.div(3)
    val y = screenInfo.screenHeight - height
    var danceState by remember { mutableStateOf(true) }

    val floatAnimation: Float by animateFloatAsState(
        if (danceState) -15f else 15f,
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
    LaunchedEffect("key") {
       // delay(100)
        danceState = danceState.not()
    }

    Image(
        painter = painterResource(id = R.drawable.other_oger), null,
        Modifier
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
            .resizeWithCenterOffset(
                width,
                height,
                x + width,
                y + 36.dp
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


@Composable
private fun BlueOger(
    screenInfo: ScreenInfo
) {

    val width = 115.dp * screenInfo.yScale
    val height = 175.dp * screenInfo.yScale

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
                screenInfo.screenWidth / 2.8f,
                screenInfo.screenHeight - width
            )
            .rotate(floatAnimation)
            .offset(dpOffset, dpOffset),
        contentScale = ContentScale.FillBounds
    )


}
