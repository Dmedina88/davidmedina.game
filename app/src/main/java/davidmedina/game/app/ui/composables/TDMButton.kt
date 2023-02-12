package davidmedina.game.app.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview()
@Composable
fun ButtonSideBySide(){
    Column {

        TDMButton()
        Button(onClick = {}) {
            Text(text = "Button", Modifier.padding(4.dp))
        }
    }
}


@Preview()
@Composable
fun TDMButton(modifier: Modifier = Modifier,text: String = "Button", onClick: () -> Unit = {}, ) {

    Box(modifier = Modifier
        .clickable(onClick = onClick)
        .drawWithCache {


            //startlocation in range of 0
            // radom point

            onDrawBehind {
//                drawLine(
//                    color = Color.Blue,
//                    start = Offset.Zero,
//                    end = Offset(size.width, size.height)
//                )

                val path = Path()
                val rect1 = Rect(Offset(18f, 12f), Size(01f, 0f))

                path.moveTo(0f, 0f)
                path.quadraticBezierTo(size.width / 3f, 13f, size.width / 2, 16f)
                path.quadraticBezierTo(size.width / 3f, 16f, size.width, 0f)
                path.arcTo(rect1, 145f, 20f, false)
                path.arcToRad(rect1, 3f, 1f, false)

                path.lineTo(size.width, 0f)

                path.lineTo(size.width, size.height)
                path.lineTo(0F, size.height)
                path.lineTo(0F, 0F)


                drawPath(
                    path, brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Magenta,
                            Color.Red,
                        )
                    )
                )

            }


        }) {
            Text(text = text, Modifier.padding(4.dp),
            color = Color.Blue, fontSize = 15.sp)
    }
}