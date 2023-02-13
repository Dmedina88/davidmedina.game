package davidmedina.game.app.features.rpg

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import davidmedina.game.app.data.models.Items
import davidmedina.game.app.ui.composables.Onlifecycal
import davidmedina.game.app.ui.drawGrid



@Composable
@Preview
fun RPGBattle() {


    val battleStateMachine = BattleStateMachine()


    battleStateMachine.init(
        listOf(
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter(),
        ), listOf(
            CharacterId.BLUE_OGER.createCharacter(),
        ), listOf(Items.Potion)
    )

    Onlifecycal(onResume = { battleStateMachine.onResume() }, {
        battleStateMachine.onPause()
    })

    val battleState by battleStateMachine.state.collectAsState()

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

            items(battleState.enemyCharacters) {
                Image(
                    painter = painterResource(id = it.characterStats.characterID.battleImage),
                    contentDescription = "",
                    contentScale = ContentScale.Fit
                )
            }
        }



        BattleMenu(
            playerCharacters = battleState.playerCharacters,
            modifier = Modifier.constrainAs(battleMenu) {
                bottom.linkTo(parent.bottom)
            })
    }

}