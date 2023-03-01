package davidmedina.game.app.features.rpg.battle.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import davidmedina.game.app.features.rpg.battle.BattleCharacter
import davidmedina.game.app.features.rpg.data.ability.getAbilityColor
import davidmedina.game.app.features.rpg.data.battleImage
import davidmedina.game.app.features.rpg.data.battleText
import davidmedina.game.app.features.rpg.data.isAlive
import davidmedina.game.app.features.rpg.data.percentage
import davidmedina.game.app.ui.composables.GradientProgressBar
import davidmedina.game.app.ui.theme.GradientColors
import davidmedina.game.app.ui.theme.shift


@Composable
fun CharacterInfo(
    characterStats: BattleCharacter, onCharacterSelected: () -> Unit,
) {

    val colorShift by animateFloatAsState(
        targetValue = if (characterStats.lastAblityUsedOn != null) 0f else 2f,
        animationSpec = repeatable(
            iterations = 15, animation = tween(durationMillis = 100), repeatMode = RepeatMode.Reverse
        )
    )
    val backgroundColor by animateColorAsState(
        targetValue = when {
            characterStats.lastAblityUsedOn != null -> {
                getAbilityColor(characterStats.lastAblityUsedOn)
            }
            characterStats.characterStats.isAlive -> {
                MaterialTheme.colorScheme.onBackground
            }
            characterStats.characterStats.isAlive.not() -> {
                Color(0xFFE74C3C)
            }
            else -> {
                MaterialTheme.colorScheme.onBackground
            }
        }
    )


    Button(
        modifier = Modifier.padding(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor.shift(if (characterStats.lastAblityUsedOn != null) colorShift else 0f)),
        onClick = onCharacterSelected
    ) {

        var startAnimation by remember {
            mutableStateOf(false)
        }
        val speedBarAnimation by animateFloatAsState(
            targetValue = if (!startAnimation) 0f
            else characterStats.speedBuilt, animationSpec = tween(durationMillis = 300)
        )
        val healthAnimation by animateFloatAsState(
            targetValue =
            if (!startAnimation) 0f
            else characterStats.characterStats.hp.percentage,
            animationSpec = tween(durationMillis = 300)
        )
        val willAnimation by animateFloatAsState(
            targetValue =
            if (!startAnimation) 0f
            else characterStats.characterStats.will.percentage,
            animationSpec = tween(durationMillis = 300)
        )

        SideEffect {
            startAnimation = true
        }

        ConstraintLayout {
            val (image, hp, will, hpBar, willBar, speed, speedBar) = createRefs()

            val textBarrier = createEndBarrier(hp, will)
            Image(
                modifier = Modifier
                    .size(75.dp)
                    .constrainAs(image) {},
                painter = painterResource(id = characterStats.characterStats.characterID.battleImage),
                contentDescription = characterStats.characterStats.name
            )

            Text(
                modifier = Modifier.constrainAs(hp) {
                    this.start.linkTo(image.end)
                    this.top.linkTo(image.top)
                },
                text = "HP : ${characterStats.characterStats.hp.battleText}"
            )



            GradientProgressBar(modifier = Modifier
                .constrainAs(hpBar) {
                    start.linkTo(textBarrier, margin = 5.dp)
                    top.linkTo(hp.top)
                    bottom.linkTo(hp.bottom)
                }
                .width(180.dp)
                .height(20.dp),
                gradientColors = GradientColors.RedGradient.colors,
                progress = healthAnimation
            )

            Text(
                modifier = Modifier.constrainAs(will) {
                    this.start.linkTo(image.end)
                    this.top.linkTo(hp.bottom, margin = 5.dp)
                },
                text = "Will : ${characterStats.characterStats.will.battleText}"
            )


            GradientProgressBar(modifier = Modifier
                .constrainAs(willBar) {
                    start.linkTo(textBarrier, margin = 5.dp)
                    top.linkTo(will.top)
                    bottom.linkTo(will.bottom)
                }
                .width(180.dp)
                .height(20.dp),
                gradientColors = GradientColors.BlueGradient.colors,
                progress = willAnimation
            )


            Text(
                modifier = Modifier.constrainAs(speed) {
                    this.start.linkTo(image.end)
                    this.top.linkTo(will.bottom, margin = 5.dp)
                },
                text = "Speed: ${characterStats.turns}"
            )

            GradientProgressBar(
                modifier = Modifier
                    .width(180.dp)
                    .height(20.dp)
                    .constrainAs(speedBar) {
                        start.linkTo(textBarrier, margin = 5.dp)
                        top.linkTo(speed.top)
                        bottom.linkTo(speed.bottom)
                        end.linkTo(willBar.end)
                    }, gradientColors = GradientColors.YellowGradient.colors,
                progress = speedBarAnimation
            )
        }
    }


}

