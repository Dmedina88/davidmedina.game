package davidmedina.game.app.features.rpg.battle

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import davidmedina.game.app.features.rpg.Ability
import davidmedina.game.app.features.rpg.attack
import davidmedina.game.app.features.rpg.battleImage
import davidmedina.game.app.features.rpg.percentage
import davidmedina.game.app.ui.composables.gameBoxBackground


@Composable
fun BattleMenu(
    playerCharacters: List<BattleCharacter>,
    modifier: Modifier = Modifier,
    onCharacterSelected: (Battler) -> Unit,

    onAbility: (Ability) -> Unit,
) {
    Row(
        modifier = modifier
            .then(Modifier)
            .fillMaxWidth()
            .gameBoxBackground()
    ) {
        AbilitySelectMenu(modifier,onAbility)

        Column(modifier.fillMaxWidth(.80f)) {
            playerCharacters.forEachIndexed { index, battleCharacter ->
                CharacterInfo(characterStats = battleCharacter, onCharacterSelected = {onCharacterSelected(Battler.Player(index = index))})
            }

        }

    }
}

@Composable
private fun AbilitySelectMenu(modifier: Modifier,    onAbility: (Ability) -> Unit,) {
    Column(
        modifier
            .fillMaxWidth(.20f)
            .gameBoxBackground()
    ) {

        Button(
            {onAbility(attack)},
            modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = "Attack")
        }
        Button(
            {}, modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = "Ability")
        }
        Button(
            {}, modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = "Item")
        }
        Button(
            {}, modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = "Item")
        }
    }
}


@Composable
fun CharacterInfo(characterStats: BattleCharacter, onCharacterSelected: () -> Unit) {


    Button(
        onClick = onCharacterSelected) {

        var startAnimation by remember() {
            mutableStateOf(false)
        }
        val speedBarAnimation by animateFloatAsState(
            targetValue =
            if (!startAnimation) 0f
            else characterStats.speedBuilt,
            animationSpec = tween(durationMillis = 300)
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
                text = "HP : ${characterStats.characterStats.hp}"
            )


            LinearProgressIndicator(modifier = Modifier
                .constrainAs(hpBar) {
                    start.linkTo(textBarrier, margin = 5.dp)
                    top.linkTo(hp.top)
                    bottom.linkTo(hp.bottom)
                }
                .width(180.dp)
                .height(20.dp),
            color = Color.Red,
            progress = healthAnimation
                )

            Text(
                modifier = Modifier.constrainAs(will) {
                    this.start.linkTo(image.end)
                    this.top.linkTo(hp.bottom, margin = 5.dp)
                },
                text = "Will : ${characterStats.characterStats.will}"
            )

            LinearProgressIndicator(modifier = Modifier
                .constrainAs(willBar) {
                    start.linkTo(textBarrier, margin = 5.dp)
                    top.linkTo(will.top)
                    bottom.linkTo(will.bottom)
                }
                .width(180.dp)
                .height(20.dp), color = Color.Blue,
                progress = willAnimation
            )


            Text(
                modifier = Modifier.constrainAs(speed) {
                    this.start.linkTo(image.end)
                    this.top.linkTo(will.bottom, margin = 5.dp)
                },
                text = "Speed: ${characterStats.turns}"
            )

            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(20.dp)
                    .constrainAs(speedBar) {
                        start.linkTo(textBarrier, margin = 5.dp)
                        top.linkTo(speed.top)
                        bottom.linkTo(speed.bottom)
                        end.linkTo(willBar.end)
                    }, color = Color.Yellow,
                progress = speedBarAnimation
            )
        }
    }
}









