package davidmedina.game.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.R

@Composable
@Preview
fun MyCanvis() {
    val image = ImageBitmap.imageResource(id = R.drawable.gen_center_17)
    val image2 = ImageBitmap.imageResource(id = R.drawable.gen_center_11)


    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        val canvasWidth = size.width
        val canvasHeight = size.height

        scale(.5f, .5f, pivot = Offset.Zero) {
                drawImage(image)

                drawImage(image2)
            }


    }
}