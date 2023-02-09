package davidmedina.game.app.ui.screens.storygame

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import davidmedina.game.app.R
import davidmedina.game.app.data.models.ArtGenAsset
import davidmedina.game.app.ui.composables.artwidget.TheHostCharacter
import davidmedina.game.app.ui.composables.noRippleClickable
import davidmedina.game.app.ui.composables.resizeWithCenterOffset
import kotlinx.coroutines.delay
import kotlin.random.Random


@Composable
@Preview
fun GodotTwoFlower() {

    // LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    //change to box with constrints
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
    ) {


        val screenWidth = this.maxWidth
        val screenHeight = this.maxHeight
        val centerX = screenWidth.div(2.dp)
        val centerY = screenHeight.div(2.dp)

        BackGround(screenHeight = screenHeight, screenWidth = screenWidth)
        Sky(screenHeight = screenHeight, screenWidth = screenWidth)
        Ground(screenHeight = screenHeight, screenWidth = screenWidth)

        Fort(screenHeight = screenHeight)


        TheHostCharacter(
            modifier = Modifier.resizeWithCenterOffset(
                200.dp,
                250.dp,
                centerX.dp,
                centerY.dp + 100.dp
            )
        )

        Trees(screenWidth = screenWidth, screenHeight = screenHeight)

        Flowers(screenHeight = screenHeight, screenWidth = screenWidth)


        Column(Modifier.background(Color.Green)) {
            Text(text = " ${screenWidth.value} x ${screenHeight.value}", fontSize = 40.sp)
        }
    }
}

@Composable
fun Fort(screenHeight: Dp) {
    Image(
        painter = painterResource(id = R.drawable.gen_structure_green_fort), null,
        Modifier.offset(24.dp, screenHeight.div(3)),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
private fun Sky(screenHeight: Dp, screenWidth: Dp) {

    Image(
        painter = painterResource(id = R.drawable.gen_sky_1), null,
        Modifier
            .height(screenHeight.div(2))
            .width(screenWidth)
            .offset(0.dp),
        contentScale = ContentScale.FillBounds
    )

}

@Composable
private fun Ground(
    screenHeight: Dp,
    screenWidth: Dp
) {
    Image(
        painter = painterResource(id = R.drawable.gen_ground_double), null,
        Modifier
            .height(screenHeight.div(2))
            .width(screenWidth)
            .offset(0.dp, screenHeight.div(2)),
        contentScale = ContentScale.FillBounds
    )
}


@Composable
private fun Clouds(
    asset: ArtGenAsset,
    intSize: IntSize
) {
    Image(
        asset.bitmap, null,
        Modifier
            .resizeWithCenterOffset(
                200.dp,
                200.dp,
                x = LocalDensity.current.run { Random.nextInt(intSize.width).toDp() },
                y = LocalDensity.current.run { Random.nextInt(intSize.height.div(2)).toDp() },
            )
    )
}

@Composable
private fun BackGround(
    screenHeight: Dp,
    screenWidth: Dp
) {
    Image(
        painter = painterResource(id = R.drawable.gen_background_1), null,
        Modifier
            .height(screenHeight)
            .width(screenWidth),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
private fun Flowers(
    screenWidth: Dp,
    screenHeight: Dp
) {
    Image(
        painter = painterResource(id = R.drawable.gen_flower_man), null,
        Modifier.offset(screenWidth - 280.dp, screenHeight - 325.dp),
        contentScale = ContentScale.FillBounds
    )

    Image(
        painter = painterResource(id = R.drawable.gen_flower_man), null,
        Modifier.offset(screenWidth - 350.dp, screenHeight - 325.dp),
        contentScale = ContentScale.FillBounds
    )
}

@Preview
@Composable
private fun Trees(
    screenWidth: Dp = 1000.dp,
    screenHeight: Dp = 800.dp,
) {

    Box(modifier = Modifier.size(screenWidth, screenHeight)) {
        val treeHeight by remember {
            mutableStateOf(screenHeight / 2)
        }
        val treeWidth by remember {
            mutableStateOf(300.dp)
        }
        val treeX by remember {
            mutableStateOf( screenWidth - treeWidth.div(2))
        }

        val treeY  by remember {
            mutableStateOf(screenHeight / 2)
        }


        Image(
            painter = painterResource(id = R.drawable.gen_land_trait_apple_tree), null,
            Modifier
                .resizeWithCenterOffset(
                    treeWidth,
                    treeHeight,
                    treeX,
                    treeY + 34.dp
                )
                .rotate(40f),
            contentScale = ContentScale.FillBounds,
        )

        Image(
            painter = painterResource(id = R.drawable.gen_land_trait_apple_tree), null,
            Modifier.resizeWithCenterOffset(
                treeWidth,
                treeHeight,
                treeX - treeWidth,
                treeY
            ),
            contentScale = ContentScale.FillBounds
        )

        var treeState by remember { mutableStateOf(true) }


        val floatAnimation: Float by animateFloatAsState(
            if (treeState) -16f else 30f,
            animationSpec = repeatable(
                iterations = 3,
                animation = tween(durationMillis = 500),
                repeatMode = RepeatMode.Reverse)
            )


        LaunchedEffect(treeState){
                delay(5000)
                treeState = treeState.not()

        }



        Image(
            painter = painterResource(id = R.drawable.gen_land_trait_apple_tree), null,
            Modifier
                .resizeWithCenterOffset(
                    treeWidth,
                    treeHeight,
                    treeX - treeWidth.div(2),
                    treeY + 34.dp
                )
                .noRippleClickable { treeState = treeState.not() }
                //.rotate(offsetAnimation)
                .graphicsLayer {
                    rotationZ = floatAnimation
                    rotationY = floatAnimation.div(2)

                },
            contentScale = ContentScale.FillBounds
        )


    }
}