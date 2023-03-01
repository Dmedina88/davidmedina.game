package davidmedina.game.app.features.rpg.battle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import davidmedina.game.app.features.rpg.battle.ui.ActionChip
import davidmedina.game.app.features.rpg.battle.ui.BattleMenu
import davidmedina.game.app.features.rpg.data.*
import davidmedina.game.app.ui.composables.Onlifecycal
import davidmedina.game.app.ui.composables.gameBoxBackground
import davidmedina.game.app.ui.composables.noRippleClickable
import davidmedina.game.app.ui.drawGrid
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel


@Composable
@Preview
fun RPGBattleScreen() {

    val battleStateMachine = koinViewModel<BattleStateMachine>()
    Onlifecycal(onResume = { battleStateMachine.onResume() }, {
        battleStateMachine.onPause()
    })

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
        BattleScreen(battleStateMachine)
    }
}

@Composable
private fun BattleScreen(battleStateMachine: BattleStateMachine) {
    AnimatedVisibility(visible = battleStateMachine.battleStage == BattleStage.BattleInProgress) {

        ConstraintLayout {
            Background()
            Column {

                EnamyView(battleStateMachine.enemyCharacters) {
                    battleStateMachine.targetSelected(
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
    BattleResult(battleStateMachine.battleStage)
}

@Composable
private fun EnamyView(enamys: List<BattleCharacter>, onTargetSelcted: (Int) -> Unit) {
    LazyRow(Modifier.fillMaxHeight(.65f)) {
        itemsIndexed(enamys) { int, enamy ->
            AnimatedVisibility(visible = enamy.characterStats.isAlive) {
                var shakeCount by remember { mutableStateOf(0) }
                val shakeAnim by animateFloatAsState(
                    targetValue = if (enamy.abilityBeingUsed != null || shakeCount < 3) (if (shakeCount % 2 == 0) 5f else -5f) else 0f,
                    animationSpec = tween(durationMillis = 50)
                )

                LaunchedEffect(shakeCount) {
                    delay(50)
                    if (shakeCount <= 3) {
                        shakeCount += 1
                    }else{
                        shakeCount=0
                    }
                }
                Image(
                    modifier = Modifier
                        .noRippleClickable {
                            onTargetSelcted(int)
                        }
                        .graphicsLayer {
                            rotationZ = if (enamy.abilityBeingUsed != null) shakeAnim else 0F
                        },
                    painter = painterResource(id = enamy.characterStats.characterID.battleImage),
                    contentDescription = "",
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun BattleResult(battleStage: BattleStage) {
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

    AnimatedVisibility(visible = battleStage == BattleStage.BattleWon) {
        Box(
            modifier = Modifier
                .gameBoxBackground()
                .fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = battleStage.toString(),
                color = Color.Yellow
            )
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
