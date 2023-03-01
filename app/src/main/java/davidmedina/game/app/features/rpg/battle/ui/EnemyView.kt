package davidmedina.game.app.features.rpg.battle.ui


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import davidmedina.game.app.features.rpg.battle.BattleCharacter
import davidmedina.game.app.features.rpg.data.battleImage
import davidmedina.game.app.features.rpg.data.isAlive
import davidmedina.game.app.ui.composables.noRippleClickable
import kotlinx.coroutines.delay

@Composable
fun EnemyView(enamys: List<BattleCharacter>, onTargetSelcted: (Int) -> Unit) {
    LazyRow(Modifier.fillMaxHeight(.65f)) {
        itemsIndexed(enamys) { int, enemy ->
            AnimatedVisibility(visible = enemy.characterStats.isAlive) {
                Enamy(enemy) { onTargetSelcted(int) }
            }
        }
    }
}

@Composable
private fun Enamy(
    enamy: BattleCharacter,
    onTargetSelcted: () -> Unit,
) {
    var shakeCount by remember { mutableStateOf(0) }
    val shakeAnim by animateFloatAsState(
        targetValue = if (enamy.abilityBeingUsed != null || shakeCount < 3) (if (shakeCount % 2 == 0) 5f else -5f) else 0f,
        animationSpec = tween(durationMillis = 50)
    )

    LaunchedEffect(shakeCount) {
        delay(50)
        if (shakeCount <= 3) {
            shakeCount += 1
        } else {
            shakeCount = 0
        }
    }
    Image(
        modifier = Modifier
            .noRippleClickable {
                onTargetSelcted()
            }
            .graphicsLayer {
                rotationZ = if (enamy.abilityBeingUsed != null) shakeAnim else 0F
            },
        painter = painterResource(id = enamy.characterStats.characterID.battleImage),
        contentDescription = "",
        contentScale = ContentScale.Fit
    )
}