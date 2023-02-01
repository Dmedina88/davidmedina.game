package davidmedina.game.app.ui.screens.worldgen

import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import davidmedina.game.app.data.models.*
import davidmedina.game.app.ui.composables.LockScreenOrientation
import davidmedina.game.app.ui.resizeWithCenterOffset
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random


@Composable
@Preview
fun BoxArtScreen() {

    val vm  : BoxArtViewModal = koinViewModel()
    var refresh by remember { mutableStateOf(1) }

    val list = buildList {
        add(ArtGenAsset.BackGround(ImageBitmap.imageResource(backgrounds.random())))
        add(ArtGenAsset.Ground(ImageBitmap.imageResource(ground.random())))
        add(ArtGenAsset.Sky(ImageBitmap.imageResource(sky.random())))
        repeat(2) {
            this.add(ArtGenAsset.Clouds(ImageBitmap.imageResource(clouds.random())))
        }

        add(ArtGenAsset.Flying(ImageBitmap.imageResource(flying.random())))

        add(ArtGenAsset.Flower(ImageBitmap.imageResource(flowers.random())))


        add(ArtGenAsset.Structures(ImageBitmap.imageResource(structures.random())))


        add(ArtGenAsset.Center(ImageBitmap.imageResource(center.random())))
    }
    ArtBox(list,vm::onAssetClicked)
}

@Composable
fun ArtBox(assets: List<ArtGenAsset> = emptyList(), onClick: (ArtGenAsset) -> Unit) {


    var intSize by remember { mutableStateOf(IntSize.Zero) }
    var screenWidth by remember { mutableStateOf(Dp(0f)) }
    var screenHeight by remember { mutableStateOf(Dp(0f)) }

    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    Box(
        Modifier
            .fillMaxSize()
            .onSizeChanged {
                intSize = it
            }) {


        screenWidth = LocalDensity.current.run { intSize.width.toDp() }
        screenHeight = LocalDensity.current.run { intSize.height.toDp() }

        if (intSize != IntSize.Zero)
            assets.forEach { asset ->
                when (asset) {
                    is ArtGenAsset.BackGround ->
                        BackGround(asset, screenHeight, screenWidth)

                    is ArtGenAsset.Center ->
                        Center(asset, screenWidth, screenHeight)
                    is ArtGenAsset.Clouds -> {
                        repeat(5) { _ ->
                            Clouds(asset, intSize)
                        }
                    }
                    is ArtGenAsset.Flower ->
                        repeat(5) { _ ->
                            Flower(asset, intSize, onClick)
                        }
                    is ArtGenAsset.Flying -> repeat(3) { _ ->

                        Flying(asset, intSize)

                    }
                    is ArtGenAsset.Ground -> {
                        Ground(asset, screenHeight, screenWidth)

                    }
                    is ArtGenAsset.GroundCreature -> TODO()
                    is ArtGenAsset.LandTrait -> TODO()
                    is ArtGenAsset.Scatter -> TODO()
                    is ArtGenAsset.Sky -> {}
/*
Image(
 it.bitmap,

 dstSize = IntSize(canvasWidth.toInt(), (canvasHeight / 2).toInt())
)

*/
                    is ArtGenAsset.Structures -> {}
/*
                    drawImage(
                        it.bitmap,
                        dstOffset = IntOffset(0, ((canvasHeight / 2) - 300).toInt()),
                        dstSize = IntSize(600, 400)
                    )

 */

                }
            }

        Column(Modifier.background(Color.Green)) {
            Text(text = intSize.toString(), fontSize = 40.sp)
            Text(text = "dp $screenWidth x $screenHeight", fontSize = 40.sp)

        }
    }
}

@Composable
private fun Ground(
    asset: ArtGenAsset,
    screenHeight: Dp,
    screenWidth: Dp
) {
    Image(
        asset.bitmap, null,
        Modifier
            .height(screenHeight.div(2))
            .width(screenWidth)
            .offset(0.dp, screenHeight.div(2)),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
private fun Flying(
    asset: ArtGenAsset,
    intSize: IntSize
) {
    Image(
        asset.bitmap, null,
        Modifier
            .resizeWithCenterOffset(
                200.dp,
                200.dp,
                x = LocalDensity.current.run {
                    Random
                        .nextInt(intSize.width)
                        .toDp()
                },
                y = LocalDensity.current.run {
                    Random
                        .nextInt(intSize.height.div(2))
                        .toDp()
                },
            )
            .background(Color.Black)
    )
}


@Composable
private fun Flower(
    asset: ArtGenAsset,
    intSize: IntSize,
    onClick: (ArtGenAsset) -> Unit
) {
    Image(
        asset.bitmap, null,
        Modifier
            .resizeWithCenterOffset(
                100.dp,
                100.dp,
                x = LocalDensity.current.run { Random.nextInt(intSize.width).toDp() },
                y = LocalDensity.current.run { Random.nextInt(intSize.height.div(2), intSize.height).toDp() },
            ).clickable {
                onClick(asset)
            }
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
private fun Center(
    asset: ArtGenAsset,
    screenWidth: Dp,
    screenHeight: Dp
) {
    Image(
        asset.bitmap, null,
        Modifier
            .resizeWithCenterOffset(
                300.dp,
                300.dp,
                x = (screenWidth / 2),
                y = (screenHeight / 2)
            )
    )
}

@Composable
private fun BackGround(
    asset: ArtGenAsset,
    screenHeight: Dp,
    screenWidth: Dp
) {
    Image(
        asset.bitmap, null,
        Modifier
            .height(screenHeight)
            .width(screenWidth),
        contentScale = ContentScale.FillBounds
    )
}