package davidmedina.game.app.ui.composables.artwidget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.R
import davidmedina.game.app.ui.drawGrid
import kotlinx.coroutines.delay

@Preview
@Composable
fun TheHostCharacter(modifier: Modifier = Modifier.size(400.dp, 500.dp)) {

    // //4/5s

    BoxWithConstraints(modifier = modifier.drawWithContent {
        drawContent()
        drawGrid()

    }) {

        val screenWidth = this.maxWidth
        val screenHeight = this.maxHeight


        var animationState by remember { mutableStateOf(true) }


        val floatAnimation by animateDpAsState(
            if (animationState) 0.dp else 6.dp,
            animationSpec = tween(1500)
        )


        LaunchedEffect(animationState) {
            delay(1450)
            animationState = animationState.not()

        }

        Image(
            painter = painterResource(id = R.drawable.host_body),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.offset(0.dp, floatAnimation)
        )

        val eyeXLeft = (screenWidth.value / 3.2f).dp
        val eyeY = (screenHeight.value / 2.855f).dp

        Image(
            painter = painterResource(id = R.drawable.host_eyebrow_right),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset(eyeXLeft, eyeY + floatAnimation)
        )
        val eyeXRight = (screenWidth.value / 1.8).dp

        Image(
            modifier = Modifier
                .offset(eyeXRight, eyeY + floatAnimation),

            painter = painterResource(id = R.drawable.host_eyebrow_left),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )


        val glasseX = screenWidth / 4
        val glassesY = (screenHeight.value / 2.8f).dp
        Image(
            modifier = Modifier
                .offset(glasseX, glassesY + floatAnimation),
            painter = painterResource(id = R.drawable.host_glasses),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )



        Image(
            modifier = Modifier
                .offset(145.dp, 150.dp + floatAnimation),
            painter = painterResource(id = R.drawable.host_eye),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Image(
            modifier = Modifier
                .offset(220.dp, 150.dp + floatAnimation),
            painter = painterResource(id = R.drawable.host_other_eye),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )


        Image(
            modifier = Modifier
                .offset(172.dp, 230.dp + floatAnimation),
            painter = painterResource(id = R.drawable.host_jow),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )


//        Column(Modifier.background(Color.Green)) {
//            Text(text = " ${screenWidth.value} x ${screenHeight.value}", fontSize = 40.sp)
//        }
//

    }

}



