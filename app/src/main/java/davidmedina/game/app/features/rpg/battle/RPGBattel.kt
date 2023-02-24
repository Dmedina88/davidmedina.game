package davidmedina.game.app.features.rpg.battle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import davidmedina.game.app.features.rpg.*
import davidmedina.game.app.ui.composables.Onlifecycal
import davidmedina.game.app.ui.composables.gameBoxBackground
import davidmedina.game.app.ui.composables.noRippleClickable
import davidmedina.game.app.ui.drawGrid
import org.koin.androidx.compose.koinViewModel


@Composable
@Preview
fun RPGBattle() {

    val battleStateMachine = koinViewModel<BattleStateMachine>()
    Onlifecycal(onResume = { battleStateMachine.onResume() }, {
        battleStateMachine.onPause()
    })

    battleStateMachine.init(
        listOf(
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(1, 20)),
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(1, 20)),
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(1, 20)),
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(1, 20)),
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(1, 20)),
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(1, 20)),
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(1, 20)),
            CharacterId.OTHER_OGER.createCharacter().copy(hp = DiminishableStates(1, 20))
        ), listOf(Items.Potion(50))
    )

    Box(
        Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        BattleScreen(battleStateMachine)
    }
}

@Composable
private fun BattleScreen(battleStateMachine: BattleStateMachine) {
    AnimatedVisibility(visible = battleStateMachine.battleStage == BattleStage.BattleInProgress) {

        ConstraintLayout {
            val (battleMenu, enemy) = createRefs()
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

    AnimatedVisibility(visible = battleStateMachine.battleStage == BattleStage.BattleLost) {
        Box(
            modifier = Modifier
                .gameBoxBackground()
                .fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "LOSER!",
                color = Color.Yellow,
                fontSize = 32.sp
            )
        }
    }

    AnimatedVisibility(visible = battleStateMachine.battleStage == BattleStage.BattleWon) {
        Box(
            modifier = Modifier
                .gameBoxBackground()
                .fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = battleStateMachine.battleStage.toString(),
                color = Color.Yellow
            )
        }
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

