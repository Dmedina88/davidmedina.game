package davidmedina.game.app.features.rpg

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import davidmedina.game.app.ui.composables.gameBoxBackground


@Composable
fun BattleMenu(
    playerCharacters: List<CharacterStats>,
    modifier: Modifier = Modifier,
    onAction: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .then(Modifier)
            .fillMaxWidth()
            .gameBoxBackground()
    ) {
        Column(
            modifier
                .fillMaxWidth(.20f)
                .gameBoxBackground()
        ) {
            Button(
                {},
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
        Column(modifier.fillMaxWidth(.80f)) {
            playerCharacters.forEach {
                CharacterInfo(characterStats = it)
            }
        }
    }
}


@Composable
fun CharacterInfo(characterStats: CharacterStats) {

    Button(
        onClick = { /*TODO*/ },

        ) {

        ConstraintLayout {
            val (image, hp, will, hpBar, willBar, speed, speedBar) = createRefs()

            val textBarrier = createEndBarrier(hp, will)
            Image(
                modifier = Modifier
                    .size(75.dp)
                    .constrainAs(image) {},
                painter = painterResource(id = characterStats.characterID.battleImage),
                contentDescription = characterStats.name
            )

            Text(
                modifier = Modifier.constrainAs(hp) {
                    this.start.linkTo(image.end)
                    this.top.linkTo(image.top)
                },
                text = "HP : ${characterStats.Hp}"
            )
            Text(
                modifier = Modifier.constrainAs(will) {
                    this.start.linkTo(image.end)
                    this.top.linkTo(hp.bottom, margin = 5.dp)
                },
                text = "Will : ${characterStats.Will}"
            )

            Box(modifier = Modifier
                .constrainAs(hpBar) {
                    start.linkTo(textBarrier, margin = 5.dp)
                    top.linkTo(hp.top)
                    bottom.linkTo(hp.bottom)
                }
                .width(180.dp)
                .height(20.dp)
                .background(Color.Red))

            Box(modifier = Modifier
                .constrainAs(willBar) {
                    start.linkTo(textBarrier, margin = 5.dp)
                    top.linkTo(will.top)
                    bottom.linkTo(will.bottom)
                }
                .width(180.dp)
                .height(20.dp)
                .background(Color.Blue))

            Text(
                modifier = Modifier.constrainAs(speed) {
                    this.start.linkTo(image.end)
                    this.top.linkTo(will.bottom, margin = 5.dp)
                },
                text = "Speed:"
            )


            Box(modifier = Modifier
                .width(180.dp)
                .height(20.dp)
                .constrainAs(speedBar) {
                    start.linkTo(textBarrier, margin = 5.dp)
                    top.linkTo(speed.top)
                    bottom.linkTo(speed.bottom)
                    end.linkTo(willBar.end)
                }
                .background(Color.Yellow))

        }
    }
}









