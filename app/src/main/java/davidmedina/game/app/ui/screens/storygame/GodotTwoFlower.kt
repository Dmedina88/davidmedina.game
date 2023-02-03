package davidmedina.game.app.ui.screens.storygame

import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import davidmedina.game.app.ui.composables.LockScreenOrientation
import davidmedina.game.app.ui.composables.TheHostCharacter
import davidmedina.game.app.ui.composables.resizeWithCenterOffset
import kotlin.random.Random


@Composable
@Preview
fun GodotTwoFlower() {


//    var intSize by remember { mutableStateOf(IntSize.Zero) }
//    var screenWidth by remember { mutableStateOf(Dp(0f)) }
//    var screenHeight by remember { mutableStateOf(Dp(0f)) }

    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    //change to box with constrints
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
    ) {


        val screenWidth = this.maxWidth
        val screenHeight = this.maxHeight


        BackGround(screenHeight = screenHeight, screenWidth = screenWidth)
        Sky(screenHeight = screenHeight, screenWidth = screenWidth)

        Ground(screenHeight = screenHeight, screenWidth = screenWidth)

        Box(Modifier.offset((screenWidth / 2) - 100.dp, screenHeight / 2).size(100.dp,100.dp)) {
            TheHostCharacter()
        }



        Trees(screenWidth = screenWidth, screenHeight = screenHeight)

        Flowers(screenHeight = screenHeight, screenWidth = screenWidth)


        Column(Modifier.background(Color.Green)) {
            Text(text = " ${screenWidth.value} x ${screenHeight.value}", fontSize = 40.sp)
        }
    }
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
        Modifier.offset(screenWidth - 180.dp, screenHeight - 225.dp),
        contentScale = ContentScale.FillBounds
    )

    Image(
        painter = painterResource(id = R.drawable.gen_flower_man), null,
        Modifier.offset(screenWidth - 250.dp, screenHeight - 225.dp),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
private fun Trees(
    screenWidth: Dp,
    screenHeight: Dp
) {

    val treeHeight = screenHeight / 3
    val treeWidth = screenWidth / 8
    val treex = screenWidth - treeWidth
    val treey = screenHeight / 2


    Image(
        painter = painterResource(id = R.drawable.gen_land_trait_apple_tree), null,
        Modifier.resizeWithCenterOffset(treeWidth, treeHeight , treex, treey),
        contentScale = ContentScale.FillBounds
    )

    Image(
        painter = painterResource(id = R.drawable.gen_land_trait_apple_tree), null,
        Modifier.resizeWithCenterOffset(
            treeWidth,
            treeHeight,
            treex -treeWidth,
            treey
        ),
        contentScale = ContentScale.FillBounds
    )

    Image(
        painter = painterResource(id = R.drawable.gen_land_trait_apple_tree), null,
        Modifier.resizeWithCenterOffset(
            treeWidth,
            treeHeight,
            treex - 250.dp,
            treey - 225.dp
        ),
        contentScale = ContentScale.FillBounds
    )
}