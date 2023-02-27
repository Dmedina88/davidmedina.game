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
import davidmedina.game.app.features.rpg.data.ability.Ability
import davidmedina.game.app.features.rpg.data.ability.DamageType
import davidmedina.game.app.features.rpg.data.ability.Stat
import davidmedina.game.app.features.rpg.data.ability.StatusEffect
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

@Composable
private fun getAbilityColor(
    ability: Ability,
): Color {
    val backgroundColor = when (ability) {
        is Ability.Offensive -> when (ability.damageType) {
            is DamageType.Physical -> Color(0xFF4F0700)
            is DamageType.Dream -> Color(0xFF460E4F)
            is DamageType.Magical -> Color(0xFF01579B)
            is DamageType.Psychic -> Color(0xFF880E4F)
        }
        is Ability.Heal -> Color(0xFF1B5E20)
        is Ability.Taunt -> Color(0xFF6A1B9A)
        is Ability.Stealth -> Color(0xFF006064)
        is Ability.Buff -> when (ability.effect) {
            is StatusEffect.Buffed -> {
                when (ability.effect.stat) {
                    Stat.Health -> Color(0xFF4A148C)
                    Stat.Strength -> Color(0xFFC62828)
                    Stat.Defense -> Color(0xFF0277BD)
                    Stat.Speed -> Color(0xFF2E7D32)
                    Stat.Mind -> Color(0xFF6A1B9A)
                }
            }
            StatusEffect.Burned -> Color(0xFFFF6D00)
            is StatusEffect.Debuffed -> Color(0xFF263238)
            StatusEffect.Frozen -> Color(0xFFBDBDBD)
            StatusEffect.Poisoned -> Color(0xFFAEEA00)
            is StatusEffect.Stunned -> Color(0xFFEC407A)
            null -> Color(0xFFEC407A)
        }
    }
    return backgroundColor
}
