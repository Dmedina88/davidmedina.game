package davidmedina.game.app.features.rpg.battle.ui


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import davidmedina.game.app.features.rpg.battle.BattleCharacter
import davidmedina.game.app.features.rpg.data.ability.getAbilityColor
import davidmedina.game.app.features.rpg.data.battleImage
import davidmedina.game.app.features.rpg.data.isAlive
import davidmedina.game.app.ui.composables.noRippleClickable
import davidmedina.game.app.ui.theme.shift
import kotlinx.coroutines.delay

@Composable
fun EnemyView(enemies: List<BattleCharacter>, onTargetSelected: (Int) -> Unit) {

    LazyRow(
        Modifier.fillMaxHeight(.65f).fillMaxWidth(),
        horizontalArrangement = Center,
    ) {
        itemsIndexed(enemies) { int, enemy ->
            AnimatedVisibility(visible = enemy.characterStats.isAlive) {
                Enemy(enemy) { onTargetSelected(int) }
            }
        }
    }
}

@Composable
private fun Enemy(
    enemy: BattleCharacter,
    onTargetSelected: () -> Unit,
) {
    var shakeCount by remember { mutableStateOf(0) }
    val shakeAnim by animateFloatAsState(
        targetValue = if (enemy.abilityBeingUsed != null || shakeCount < 3) (if (shakeCount % 2 == 0) 5f else -5f) else 0f,
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

    val colorShift by animateFloatAsState(
        targetValue = if (enemy.lastAbilityUsedOn != null) 0f else 2f,
        animationSpec = repeatable(
            iterations = 15,
            animation = tween(durationMillis = 100),
            repeatMode = RepeatMode.Reverse
        )
    )
    Image(
        modifier = Modifier
            .noRippleClickable {
                onTargetSelected()
            }
            .graphicsLayer {
                rotationZ = if (enemy.abilityBeingUsed != null) shakeAnim else 0F
            }
            .background(
                if (enemy.lastAbilityUsedOn != null) getAbilityColor(enemy.lastAbilityUsedOn).shift(
                    colorShift
                ) else Color.Transparent
            ),
        painter = painterResource(id = enemy.characterStats.characterID.battleImage),
        contentDescription = "",
        contentScale = ContentScale.Fit
    )
}