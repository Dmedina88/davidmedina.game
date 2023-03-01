package davidmedina.game.app.features.rpg.battle.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import davidmedina.game.app.features.rpg.battle.BattleCharacter
import davidmedina.game.app.features.rpg.data.ability.Ability
import davidmedina.game.app.features.rpg.data.ability.attack
import davidmedina.game.app.ui.composables.gameBoxBackground


@Composable
fun AbilitySelectMenu(
    selectedCharacter: BattleCharacter?,
    onAbility: (Ability) -> Unit
) {
    // Define a state for showing/hiding the popover
    val showPopover = remember { mutableStateOf(false) }

    // Define a state for storing the selected ability

    Row() {

        Column(
            Modifier
                .gameBoxBackground()
        ) {
            Button(
                { onAbility(attack) },
            ) {
                Text(text = "Attack")
            }

            // Add a Button for the "Ability" option, which toggles the showPopover state
            Button(
                onClick = {
                    showPopover.value = !showPopover.value
                },

                ) {
                Text(text = "Ability")
            }

            Button(
                {},

                ) {
                Text(text = "Item")
            }

            Button(
                {},
                ) {
                Text(text = "Run")
            }
        }

        // If showPopover state is true, show the popover
        if (showPopover.value && selectedCharacter != null) {
            AbilityPopover(showPopover, selectedCharacter, onAbility)
        }
    }
}


