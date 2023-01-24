package davidmedina.game.app.screens.worldgen

import android.content.pm.ActivityInfo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import davidmedina.game.app.data.models.*
import davidmedina.game.app.ui.composables.LockScreenOrientation
import kotlin.random.Random


@Composable
@Preview
fun ArtScreen() {
    val list = buildList {
        add(ArtGenAsset.BackGround(ImageBitmap.imageResource(backgrounds.random())))
        add(ArtGenAsset.Ground(ImageBitmap.imageResource(ground.random())))
        add(ArtGenAsset.Sky(ImageBitmap.imageResource(sky.random())))
        repeat(2) {
            this.add(ArtGenAsset.Clouds(ImageBitmap.imageResource(clouds.random())))
        }

        add(ArtGenAsset.Flying(ImageBitmap.imageResource(flying.random())))

        add(ArtGenAsset.Structures(ImageBitmap.imageResource(structures.random())))

        repeat(3) {
            this.add(ArtGenAsset.Flower(ImageBitmap.imageResource(flowers.random())))
        }
        add(ArtGenAsset.Center(ImageBitmap.imageResource(center.random())))
    }
    ArtCanvas(list)
}

@Composable
@Preview
fun ArtCanvas(assets: List<ArtGenAsset> = emptyList()) {

    val flowerSize = IntSize(
        with(LocalDensity.current) { 100.dp.toPx().toInt() },
        with(LocalDensity.current) { 140.dp.toPx().toInt() })


    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        val canvasWidth = size.width
        val canvasHeight = size.height

        assets.forEach {
            when (it) {
                is ArtGenAsset.BackGround ->
                    drawImage(
                        it.bitmap,
                        dstSize = IntSize(canvasWidth.toInt(), canvasHeight.toInt())
                    )

                is ArtGenAsset.Center ->

                    scale(.5f, .5f, pivot = Offset.Zero) {
                        drawImage(
                            it.bitmap,
                            dstOffset = IntOffset(this.center.x.toInt(), this.center.y.toInt())
                        )
                    }

                is ArtGenAsset.Clouds -> {
                    repeat(5) { _ ->
                        drawImage(
                            it.bitmap,
                            dstOffset = IntOffset(
                                Random.nextInt(-it.bitmap.width, canvasWidth.toInt()),
                                Random.nextInt(0, (canvasHeight / 2).toInt())
                            )
                        )

                    }
                }
                is ArtGenAsset.Flower ->
                    repeat(5) { _ ->
                        drawImage(
                            it.bitmap,
                            dstOffset = IntOffset(
                                Random.nextInt(-it.bitmap.width, canvasWidth.toInt()),
                                Random.nextInt((canvasHeight / 2).toInt(), canvasHeight.toInt())
                            ),
                            dstSize = flowerSize
                        )

                    }
                is ArtGenAsset.Flying -> repeat(3) { _ ->
                    drawImage(
                        it.bitmap,
                        dstSize = IntSize(200, 200),
                        dstOffset = IntOffset(
                            Random.nextInt(-it.bitmap.width, canvasWidth.toInt()),
                            Random.nextInt(-it.bitmap.height, (canvasHeight / 2).toInt())
                        )
                    )
                }
                is ArtGenAsset.Ground ->
                    drawImage(
                        it.bitmap,
                        dstOffset = IntOffset(0, (canvasHeight / 2).toInt()),
                        dstSize = IntSize(canvasWidth.toInt(), (canvasHeight / 2).toInt())
                    )

                is ArtGenAsset.GroundCreature -> TODO()
                is ArtGenAsset.LandTrait -> TODO()
                is ArtGenAsset.Scatter -> TODO()
                is ArtGenAsset.Sky ->
                    drawImage(
                        it.bitmap,
                        dstSize = IntSize(canvasWidth.toInt(), (canvasHeight / 2).toInt())
                    )


                is ArtGenAsset.Structures ->

                    drawImage(
                        it.bitmap,
                        dstOffset = IntOffset(0, ((canvasHeight / 2) - 300).toInt()),
                        dstSize = IntSize(600, 400)
                    )

            }
        }


    }
}