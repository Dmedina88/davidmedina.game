package davidmedina.game.app.features.rpg.data.ability

import androidx.compose.ui.graphics.Color


fun getAbilityColor(
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
