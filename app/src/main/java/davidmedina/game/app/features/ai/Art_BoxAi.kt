package davidmedina.game.app.features.ai

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.data.models.*

@Preview
@Composable
fun ArtBox_AI() {
    val assetList = buildList {
        add(ArtGenAsset.BackGround(ImageBitmap.imageResource(backgrounds.random())))
        add(ArtGenAsset.Ground(ImageBitmap.imageResource(ground.random())))
        add(ArtGenAsset.Sky(ImageBitmap.imageResource(sky.random())))
        repeat(2) {
            add(ArtGenAsset.Clouds(ImageBitmap.imageResource(clouds.random())))
            add(ArtGenAsset.Flying(ImageBitmap.imageResource(scatter.random())))
        }
        add(ArtGenAsset.Flying(ImageBitmap.imageResource(creature.random())))
        add(ArtGenAsset.Flying(ImageBitmap.imageResource(landTrait.random())))

        add(ArtGenAsset.Flying(ImageBitmap.imageResource(flying.random())))
        add(ArtGenAsset.Flower(ImageBitmap.imageResource(flowers.random())))
        add(ArtGenAsset.Structures(ImageBitmap.imageResource(structures.random())))
        add(ArtGenAsset.Center(ImageBitmap.imageResource(center.random())))
    }
    DisplayScene(assetList)
}

@Composable
private fun DisplayScene(assetList: List<ArtGenAsset>) {
    val screenDimensions = LocalConfiguration.current
    val screenWidth = screenDimensions.screenWidthDp
    val screenHeight = screenDimensions.screenHeightDp
    val imageWidth = (screenWidth / 5f)
    val imageHeight = (screenHeight / 5f)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.height(imageHeight.times(3).dp)
        ) {
            assetList.filterIsInstance<ArtGenAsset.BackGround>().forEach {
                Image(
                    bitmap = it.bitmap,
                    contentDescription = "background",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
            assetList.filterIsInstance<ArtGenAsset.Sky>().forEach {
                Image(
                    bitmap = it.bitmap,
                    contentDescription = "sky",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
            assetList.filterIsInstance<ArtGenAsset.Clouds>().forEach {
                Image(
                    bitmap = it.bitmap,
                    contentDescription = "clouds",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
            assetList.filterIsInstance<ArtGenAsset.Structures>().forEach {
                Image(
                    bitmap = it.bitmap,
                    contentDescription = "structures",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
            assetList.filterIsInstance<ArtGenAsset.Center>().forEach {
                Image(
                    bitmap = it.bitmap,
                    contentDescription = "center",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
            assetList.filterIsInstance<ArtGenAsset.Flying>().forEach {
                Image(
                    bitmap = it.bitmap,
                    contentDescription = "flying",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Box(
            modifier = Modifier.height(imageHeight.times(2).dp)
        ) {
            assetList.filterIsInstance<ArtGenAsset.Ground>().forEach {
                Image(
                    bitmap = it.bitmap,
                    contentDescription = "ground",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
            assetList.filterIsInstance<ArtGenAsset.GroundCreature>().forEach {
                Image(
                    bitmap = it.bitmap,
                    contentDescription = "ground creature",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
            assetList.filterIsInstance<ArtGenAsset.LandTrait>().forEach {
                Image(
                    bitmap = it.bitmap,
                    contentDescription = "land trait",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
            assetList.filterIsInstance<ArtGenAsset.Scatter>().forEach {
                Image(
                    bitmap = it.bitmap,
                    contentDescription = "scatter",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
            assetList.filterIsInstance<ArtGenAsset.Flower>().forEach {
                Image(
                    bitmap = it.bitmap,
                    contentDescription = "flower",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
