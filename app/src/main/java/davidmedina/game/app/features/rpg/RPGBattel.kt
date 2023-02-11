package davidmedina.game.app.features.rpg

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import davidmedina.game.app.R
import davidmedina.game.app.ui.drawGrid


@Composable
@Preview
fun RPGBattle() {

    ConstraintLayout() {

        val (battleMenu, enemy) = createRefs()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                //  rotationZ = 40f
                rotationX = 80f
            }
            .drawBehind {
                drawGrid()
            })

       // LazyRow(content = )

        Image(
            painter = painterResource(id = R.drawable.other_oger),
            contentDescription = "",
            modifier = Modifier.constrainAs(enemy) { centerTo(parent) },
            contentScale = ContentScale.Fit
        )

        BattleMenu(modifier = Modifier.constrainAs(battleMenu) {
            bottom.linkTo(parent.bottom)
        })
    }

}