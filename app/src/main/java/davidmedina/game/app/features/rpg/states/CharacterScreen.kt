package davidmedina.game.app.features.rpg.states

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.features.rpg.Character
import davidmedina.game.app.features.rpg.CharacterId
import davidmedina.game.app.features.rpg.DiminishableStates
import davidmedina.game.app.features.rpg.percentage

val mockCharacters = listOf(
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
        will = DiminishableStates(100, 100),
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
fun CharacterScreen(character: Character = mockCharacters[1]) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = character.name,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Level ${character.level}",
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = "ID: ${character.characterID}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Health: ${character.hp.current}/${character.hp.max}",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = character.hp.percentage,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Willpower: ${character.will.current}/${character.will.max}",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = character.will.percentage,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
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
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Experience: ${character.exp}/${character.level * 10}",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = character.exp.toFloat() / (character.level * 10),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
