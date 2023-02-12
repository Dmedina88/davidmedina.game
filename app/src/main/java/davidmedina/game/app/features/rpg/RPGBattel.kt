package davidmedina.game.app.features.rpg

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import davidmedina.game.app.ui.drawGrid


class BattleState(val enemys: List<CharacterStats>, val playerCharacter: List<CharacterStats>)

@Composable
@Preview
fun RPGBattle() {

    val battleState = BattleState(
        listOf(
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter(),
        ),
        listOf(CharacterId.BLUE_OGER.createCharacter())
    )
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

        LazyRow(Modifier.constrainAs(enemy) {
            centerTo(parent)
            bottom.linkTo(battleMenu.top)
        }) {

            items(battleState.enemys) {
                Image(
                    painter = painterResource(id = it.characterID.battleImage),
                    contentDescription = "",
                    contentScale = ContentScale.Fit
                )
            }
        }



        BattleMenu(
            playerCharacters = battleState.playerCharacter,
            modifier = Modifier.constrainAs(battleMenu) {
                bottom.linkTo(parent.bottom)
            })
    }

}