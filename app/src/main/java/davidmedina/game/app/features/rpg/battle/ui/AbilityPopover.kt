package davidmedina.game.app.features.rpg.battle.ui
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import davidmedina.game.app.features.rpg.battle.BattleCharacter
import davidmedina.game.app.features.rpg.data.ability.*
import davidmedina.game.app.ui.composables.gameBoxBackground


@Composable
fun AbilityPopover(
    showPopover: MutableState<Boolean>,
    battleCharacter: BattleCharacter,
    onAbility: (Ability) -> Unit
) {
    val abilityList = battleCharacter.characterStats.ability

    Box(
        Modifier
            .gameBoxBackground()
            .clickable { showPopover.value = false }, // Hide the popover when background is clicked
    ) {
        // Show the list of abilities in a Grid with two columns
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
        ) {
            items(abilityList) {
                AbilityButton(
                    canAfford = it.cost <= battleCharacter.characterStats.will.current,
                    ability = it,
                    onAbility = onAbility,
                    showPopover = showPopover
                )
            }
        }
    }
}

@Composable
private fun AbilityButton(
    canAfford : Boolean,
    ability: Ability,
    onAbility: (Ability) -> Unit,
    showPopover: MutableState<Boolean>
) {

    val backgroundColor = getAbilityColor(ability)

    Button(
        enabled = canAfford,
        onClick = {
            onAbility(ability) // Call onAbility callback with the selected ability
            showPopover.value = false // Hide the popover
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        )
    ) {
        Text(text = "${ability.name} ${ability.cost}")
    }
}

