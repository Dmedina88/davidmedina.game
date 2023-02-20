package davidmedina.game.app.features.rpg.battle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import davidmedina.game.app.data.models.Items
import davidmedina.game.app.features.rpg.CharacterId
import davidmedina.game.app.features.rpg.DiminishableStates
import davidmedina.game.app.features.rpg.battleImage
import davidmedina.game.app.features.rpg.createCharacter
import davidmedina.game.app.ui.composables.Onlifecycal
import davidmedina.game.app.ui.composables.noRippleClickable
import davidmedina.game.app.ui.drawGrid


@Composable
@Preview
fun RPGBattle() {

    val battleStateMachine = BattleStateMachine()
    Onlifecycal(onResume = { battleStateMachine.onResume() }, {
        battleStateMachine.onPause()
    })

    battleStateMachine.init(
        listOf(
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(2, 20)),
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(3, 20)),
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(1, 20)),
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(1, 20)),
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(1, 20)),

            ), listOf(
            CharacterId.BLUE_OGER.createCharacter(),
        ), listOf(Items.Potion)
    )


        BattleScreen(battleStateMachine)



}


@Composable
private fun BattleScreen(battleStateMachine: BattleStateMachine) {
    ConstraintLayout {

        val (battleMenu, enemy, actionChip) = createRefs()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        Box(
            modifier = Modifier
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

            itemsIndexed(battleStateMachine.enemyCharacters) { int, enamy ->
                AnimatedVisibility(visible = enamy.characterStats.isAlive) {
                    Image(
                        modifier = Modifier.noRippleClickable {
                            battleStateMachine.targetSelected(Battler.Enemy(int))
                        },
                        painter = painterResource(id = enamy.characterStats.characterID.battleImage),
                        contentDescription = "",
                        contentScale = ContentScale.Fit
                    )
                }

            }
        }



        BattleMenu(
            playerCharacters = battleStateMachine.playerCharacters,
            modifier = Modifier.constrainAs(battleMenu) {
                top.linkTo(enemy.bottom)
                bottom.linkTo(parent.bottom)
            },
            onCharacterSelected = battleStateMachine::characterSelected,
            onAbility = battleStateMachine::onAbilitySelected
        )
        ActionChip(battleStateMachine = battleStateMachine)

    }
}

@Composable
fun ActionChip(battleStateMachine: BattleStateMachine) {


    Row(modifier = Modifier.padding(34.dp)) {
        AnimatedVisibility(visible = battleStateMachine.currentPlayerAction?.attacker != null) {
            val character =
                battleStateMachine.playerCharacters[battleStateMachine.currentPlayerAction?.attacker?.index
                    ?: 0]
            Image(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = character.characterStats.characterID.battleImage),
                contentDescription = ""
            )
        }

        AnimatedVisibility(visible = battleStateMachine.currentPlayerAction?.ability != null) {
            Text(
                color = Color.Yellow,
                text = battleStateMachine.currentPlayerAction?.ability?.name.toString()
            )
        }
    }
}

