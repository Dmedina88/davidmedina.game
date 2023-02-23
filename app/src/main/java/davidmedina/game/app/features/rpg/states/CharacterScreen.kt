package davidmedina.game.app.features.rpg.states

import androidx.compose.foundation.Image
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
import davidmedina.game.app.features.rpg.*
import davidmedina.game.app.ui.GradientColors
import davidmedina.game.app.ui.composables.GradientProgressBar

private val mockCharacters = listOf(
    Character(
        "John Doe",
        CharacterId.Berserker,
        DiminishableStates(50, 100),
        DiminishableStates(60, 100),
        10,
        20,
        3.5f,
        5,
        exp = 10
    ),
    Character(
        "Jane Smith",
        CharacterId.Wizard,
        DiminishableStates(80, 80),
        DiminishableStates(70, 70),
        5,
        15,
        2.5f,
        10
    ),
    Character(
        "Bob Johnson",
        CharacterId.Paladin,
        DiminishableStates(100, 100),
        DiminishableStates(50, 50),
        15,
        25,
        4.0f,
        8
    ),
    Character(
        name = "Warrior",
        characterID = CharacterId.BLUE_OGER,
        hp = DiminishableStates(100, 100),
        will = DiminishableStates(50, 50),
        strength = 20,
        defense = 10,
        speed = 1.5f,
        mind = 5,
        exp = 0,
        level = 1,
    ),
    Character(
        name = "Mage",
        characterID = CharacterId.BLUE_OGER,
        hp = DiminishableStates(50, 50),
        will = DiminishableStates(30, 100),
        strength = 5,
        defense = 5,
        speed = 1.0f,
        mind = 20,
        exp = 0,
        level = 1,
    ),
    Character(
        name = "Rogue",
        characterID = CharacterId.BLUE_OGER,
        hp = DiminishableStates(75, 75),
        will = DiminishableStates(75, 75),
        strength = 10,
        defense = 7,
        speed = 2.0f,
        mind = 10,
        exp = 0,
        level = 1,
    ),
    // Add more characters as needed
)
@Preview
@Composable
fun CharacterScreen(characters: List<Character> = mockCharacters) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
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
                    text = "Experience: ${character.exp}/${character.level * 10}",
                    style = MaterialTheme.typography.bodyMedium
                )
                GradientProgressBar(
                    progress = character.exp.toFloat() / (character.level * 10),
                    gradientColors = GradientColors.GreenGradient.colors,
                    modifier = Modifier
                        .width(250.dp)
                        .height(20.dp)
                )

            }

        }
    }
}
