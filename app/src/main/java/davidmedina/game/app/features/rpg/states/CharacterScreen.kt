package davidmedina.game.app.features.rpg.states

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.R
import davidmedina.game.app.features.rpg.data.*
import davidmedina.game.app.ui.theme.GradientColors
import davidmedina.game.app.ui.composables.GradientProgressBar
import davidmedina.game.app.ui.composables.gameBoxBackground
import org.koin.androidx.compose.koinViewModel

private val mockCharacters = createMockCharacters(6)


@Preview
@Composable
fun CharacterMenuScreen(onMenuClosed: () -> Unit = {}) {
    val vm = koinViewModel<CharacterViewModel>()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .gameBoxBackground()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.mipmap.heart),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clickable { onMenuClosed() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Character Menu",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        CharacterGrid(vm.playerCharacters)
    }


}

@Composable
fun CharacterGrid(characters: List<Character> = mockCharacters) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxHeight(),
        columns = GridCells.Fixed(2)
    ) {
        items(characters) { character ->
            CharacterCard(character)
        }
    }
}

@Composable
fun CharacterCard(character: Character) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = character.characterID.battleImage),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Level ${character.level}",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "ID: ${character.characterID}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Health: ${character.hp.battleText}",
                    style = MaterialTheme.typography.bodyMedium
                )

                GradientProgressBar(
                    progress = character.hp.percentage,
                    gradientColors = GradientColors.RedGradient.colors,
                    modifier = Modifier
                        .width(250.dp)
                        .height(20.dp)
                )
                Text(
                    text = "Willpower: ${character.will.battleText}",
                    style = MaterialTheme.typography.bodyMedium
                )
                GradientProgressBar(
                    progress = character.will.percentage,
                    gradientColors = GradientColors.BlueGradient.colors,
                    modifier = Modifier
                        .width(250.dp)
                        .height(20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Strength: ${character.strength}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Defense: ${character.defense}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Speed: ${character.speed}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Mind: ${character.mind}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Experience: ${character.exp}/${character.nextLevel}",
                    style = MaterialTheme.typography.bodyMedium
                )
                GradientProgressBar(
                    progress = character.exp.toFloat() / (character.level * character.nextLevel),
                    gradientColors = GradientColors.GreenGradient.colors,
                    modifier = Modifier
                        .width(250.dp)
                        .height(20.dp)
                )

            }

        }
    }
}
