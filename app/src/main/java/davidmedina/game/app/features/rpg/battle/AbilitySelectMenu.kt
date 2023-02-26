package davidmedina.game.app.features.rpg.battle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import davidmedina.game.app.features.rpg.Ability
import davidmedina.game.app.features.rpg.DamageType
import davidmedina.game.app.features.rpg.attack
import davidmedina.game.app.ui.composables.gameBoxBackground


@Composable
fun AbilitySelectMenu(
    modifier: Modifier,
    abilityList: List<Ability>,
    onAbility: (Ability) -> Unit
) {
    // Define a state for showing/hiding the popover
    val showPopover = remember { mutableStateOf(false) }

    // Define a state for storing the selected ability

    Column(
        modifier
            .fillMaxWidth(.20f)
            .gameBoxBackground()
    ) {
        Button(
            { onAbility(attack) },
            modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = "Attack")
        }

        // Add a Button for the "Ability" option, which toggles the showPopover state
        Button(
            {
                showPopover.value = !showPopover.value
            },
            modifier
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

    // If showPopover state is true, show the popover
    if (showPopover.value) {
        abilityPopover(showPopover, abilityList, onAbility)
    }
}


@Composable
private fun abilityPopover(
    showPopover: MutableState<Boolean>,
    abilityList: List<Ability>,
    onAbility: (Ability) -> Unit
) {
    Box(
        Modifier
            .clickable { showPopover.value = false }, // Hide the popover when background is clicked
    ) {
        // Show the list of abilities in a Grid with two columns
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
        ) {
            items(abilityList.size) { index ->
                abilityButton(
                    ability = abilityList[index],
                    onAbility = onAbility,
                    showPopover = showPopover
                )
            }
        }
    }
}
@Composable
private fun abilityButton(
    ability: Ability,
    onAbility: (Ability) -> Unit,
    showPopover: MutableState<Boolean>
) {
    val backgroundColor = when (ability) {
        is Ability.Offensive<*> -> when (ability.damageType) {
            is DamageType.Physical -> Color(0xFF4F0700)
            is DamageType.Dream -> Color(0xFF460E4F)
            is DamageType.Poison -> Color(0xFF1B5E20)
            is DamageType.Fire -> Color(0xFF7F0000)
            is DamageType.Ice -> Color(0xFF0D47A1)
            is DamageType.Lightning -> Color(0xFF6A1B9A)
            is DamageType.Holy -> Color(0xFFA00000)
            is DamageType.Dark -> Color(0xFF3E2723)
            is DamageType.Bleed -> Color(0xFFB0003A)
            is DamageType.Burn -> Color(0xFFE65100)
            is DamageType.Freeze -> Color(0xFF01579B)
            is DamageType.Shock -> Color(0xFF880E4F)
        }
        is Ability.Heal -> Color(0xFF1B5E20)
        is Ability.Buff -> Color(0xFF9E9D24)
        is Ability.Debuff -> Color(0xFF424242)
        is Ability.Taunt -> Color(0xFF6A1B9A)
        is Ability.Stealth -> Color(0xFF006064)
    }

    Button(
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
        Text(text = ability.name)
    }
}
