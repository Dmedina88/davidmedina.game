package davidmedina.game.app.features.rpg;

sealed class Ability(open val name: String) {
    data class Offensive<T : DamageType>(override val name: String, val damageType: T) :
        Ability(name)

    data class Heal(override val name: String, val healAmount: Int) : Ability(name)

    data class Buff(override val name: String, val stat: Stat, val buffAmount: Int) : Ability(name)

    data class Debuff(override val name: String, val stat: Stat, val debuffAmount: Int) :
        Ability(name)

    data class Taunt(override val name: String) : Ability(name)

    data class Stealth(override val name: String) : Ability(name)
}

sealed class DamageType {
    data class Physical(val damageFactor: Float) : DamageType()
    data class Dream(val damageFactor: Float) : DamageType()
    data class Poison(val damageFactor: Float, val procValue: Float) : DamageType()
    data class Fire(val damageFactor: Float, val procValue: Float) : DamageType()
    data class Ice(val damageFactor: Float, val procValue: Float) : DamageType()
    data class Lightning(val damageFactor: Float, val procValue: Float) : DamageType()
    data class Holy(val damageFactor: Float, val procValue: Float) : DamageType()
    data class Dark(val damageFactor: Float, val procValue: Float) : DamageType()
    data class Bleed(val damageFactor: Float, val procValue: Float) : DamageType()
    data class Burn(val damageFactor: Float, val procValue: Float) : DamageType()
    data class Freeze(val damageFactor: Float, val procValue: Float) : DamageType()
    data class Shock(val damageFactor: Float, val procValue: Float) : DamageType()
}

val attack = Ability.Offensive("Attack", DamageType.Physical(1f))
val fireball = Ability.Offensive("Fireball", DamageType.Fire(1f, 0.3f))
val heal = Ability.Heal("Heal", 15)
val buffAttack = Ability.Buff("Buff Attack", Stat.Attack, 5)
val debuffDefense = Ability.Debuff("Debuff Defense", Stat.Defense, -3)
val taunt = Ability.Taunt("Taunt")
val stealth = Ability.Stealth("Stealth")


sealed class StatusEffect {
    object Poisoned : StatusEffect()
    object Burned : StatusEffect()
    object Frozen : StatusEffect()
    data class Stunned(val turnsRemaining: Int) : StatusEffect()
    data class Buffed(val stat: Stat, val buffAmount: Int, val turnsRemaining: Int) : StatusEffect()
    data class Debuffed(val stat: Stat, val debuffAmount: Int, val turnsRemaining: Int) :
        StatusEffect()
}

sealed class Stat {
    object Health : Stat()
    object Attack : Stat()
    object Defense : Stat()
    object Speed : Stat()
    object Mind : Stat()
}