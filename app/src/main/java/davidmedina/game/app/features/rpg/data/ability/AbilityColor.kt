package davidmedina.game.app.features.rpg.data.ability

import androidx.compose.ui.graphics.Color


fun getAbilityColor(
    ability: Ability,
): Color {
    val backgroundColor = when (ability) {
        is Ability.Offensive -> when (ability.damageType) {
            is DamageType.Physical -> Color(0xFFA30000)
            is DamageType.Dream -> Color(0xFF450057)
            is DamageType.Magical -> Color(0xFF0077C2)
            is DamageType.Psychic -> Color(0xFFAD1457)
            DamageType.Energy -> Color(0xFFE65100)
        }
        is Ability.Heal -> Color(0xFF2E7D32)
        is Ability.Taunt -> Color(0xFF880E4F)
        is Ability.Stealth -> Color(0xFF009688)
        is Ability.Buff -> when (ability.effect) {
            is StatusEffect.Buffed -> {
                when (ability.effect.stat) {
                    Stat.Health -> Color(0xFFD50000)
                    Stat.Strength -> Color(0xFF6A1B9A)
                    Stat.Defense -> Color(0xFF01579B)
                    Stat.Speed -> Color(0xFF388E3C)
                    Stat.Mind -> Color(0xFF4A148C)
                }
            }
            StatusEffect.Burned -> Color(0xFFFF9100)
            is StatusEffect.Debuffed -> Color(0xFF37474F)
            StatusEffect.Frozen -> Color(0xFFE0E0E0)
            StatusEffect.Poisoned -> Color(0xFFC6FF00)
            is StatusEffect.Stunned -> Color(0xFFD81B60)
            null -> Color(0xFFD81B60)
        }
    }
    return backgroundColor
}



