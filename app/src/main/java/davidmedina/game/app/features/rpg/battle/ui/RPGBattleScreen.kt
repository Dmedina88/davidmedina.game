package davidmedina.game.app.features.rpg.battle.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import davidmedina.game.app.features.rpg.battle.BattleStage
import davidmedina.game.app.features.rpg.battle.BattleStateMachine
import davidmedina.game.app.features.rpg.battle.Battler
import davidmedina.game.app.features.rpg.data.CharacterId
import davidmedina.game.app.features.rpg.data.Items
import davidmedina.game.app.features.rpg.data.createCharacter
import davidmedina.game.app.ui.composables.Onlifecycal
import davidmedina.game.app.ui.composables.gameBoxBackground
import davidmedina.game.app.ui.drawGrid
import org.koin.androidx.compose.koinViewModel


@Composable
@Preview
fun RPGBattleScreen(onBattleWon: () -> Unit = {}) {

    val battleStateMachine = koinViewModel<BattleStateMachine>()
    Onlifecycal(
        onResume = { battleStateMachine.systemPause(false) },
        onPause = { battleStateMachine.systemPause(true) }
    )

    battleStateMachine.init(
        listOf(
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter(),
            CharacterId.OTHER_OGER.createCharacter()
        ), listOf(Items.Potion(50))
    )

    Box(
        Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        BattleScreen(battleStateMachine) {
            onBattleWon()
        }
    }
}

@Composable
private fun BattleScreen(battleStateMachine: BattleStateMachine, onBattleWon: () -> Unit) {
    AnimatedVisibility(visible = battleStateMachine.battleStage == BattleStage.BattleInProgress) {

        ConstraintLayout {
            Background()
            Column {
                EnemyView(battleStateMachine.enemyCharacters) {
                    battleStateMachine.enemySelected(
                        Battler.Enemy(it)
                    )
                }
                BattleMenu(
                    modifier = Modifier.fillMaxHeight(),
                    playerCharacters = battleStateMachine.playerCharacters,
                    onCharacterSelected = battleStateMachine::characterSelected,
                    onAbility = battleStateMachine::onAbilitySelected,
                    selectedCharacter = battleStateMachine.selectedCharacter,
                )
            }
            ActionChip(battleStateMachine = battleStateMachine)
        }
    }
    BattleResult(battleStateMachine.battleStage) {
        onBattleWon()
    }
}


@Composable
private fun BattleResult(battleStage: BattleStage, onBattleWon: () -> Unit) {
    AnimatedVisibility(visible = battleStage == BattleStage.BattleLost) {
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

    //move to ex ittem screen? i victory vm
    AnimatedVisibility(visible = battleStage == BattleStage.BattleWon) {
        Box(
            modifier = Modifier
                .gameBoxBackground()
                .fillMaxSize()
        ) {
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = onBattleWon
            ) {
                Text(

                    text = "WINNER",
                    color = Color.Yellow
                )
            }

        }
    }
}

@Composable
private fun Background() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationX = 80f
            }
            .drawBehind {
                drawGrid()
            })
}