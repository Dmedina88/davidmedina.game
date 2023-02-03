package davidmedina.game.app.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import davidmedina.game.app.R

@Preview
@Composable
fun TheHostCharacter() {

    // 810/1000
    val size = DpSize(100.dp, 175.dp)

    BoxWithConstraints() {


        Image(
          //  modifier = Modifier.size(size),
            painter = painterResource(id = R.drawable.host_body),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .offset(135.dp, 125.dp)
                .paint(painterResource(id = R.drawable.host_eyebrow_right))
        )

        Box(
            modifier = Modifier
                .offset(210.dp, 125.dp)
                .paint(painterResource(id = R.drawable.host_eyebrow_left))
        )

        Box(
            modifier = Modifier
                .offset(120.dp, 130.dp)
                .paint(painterResource(id = R.drawable.host_glasses))
        )

        Box(
            modifier = Modifier
                .offset(145.dp, 150.dp)
                .paint(painterResource(id = R.drawable.host_eye))
        )

        Box(
            modifier = Modifier
                .offset(220.dp, 150.dp)
                .paint(painterResource(id = R.drawable.host_eye))
        )


        Box(
            modifier = Modifier
                .offset(172.dp, 230.dp)
                .paint(painterResource(id = R.drawable.host_jow))
        )

    }
}



