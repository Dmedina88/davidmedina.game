package davidmedina.game.app.features.rpg.ability;

sealed class Ability(open val name: String, open val cost: Int = 0) {
    data class Offensive<T : DamageType>(
        override val name: String,
        val damageType: T,
        override val cost: Int
    ) :
        Ability(name, cost)

    data class Heal(override val name: String, val healAmount: Int, override val cost: Int) :
        Ability(name, cost)

    data class Buff(
        override val name: String,
        val stat: Stat,
        val buffAmount: Int,
        override val cost: Int
    ) :
        Ability(name, cost)

    data class Debuff(
        override val name: String,
        val stat: Stat,
        val debuffAmount: Int,
        override val cost: Int
    ) :
        Ability(name, cost)

    data class Taunt(override val name: String, override val cost: Int) : Ability(name, cost)
    data class Stealth(override val name: String, override val cost: Int) : Ability(name, cost)
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

sealed class StatusEffect {
    object Poisoned : StatusEffect() //damage over time?
    object Burned : StatusEffect() // damage over time?
    object Frozen : StatusEffect() // incress will cost of ablity
    data class Stunned(val turnsRemaining: Int) : StatusEffect()
    data class Buffed(val stat: Stat, val buffAmount: Int, val turnsRemaining: Int) : StatusEffect()
    data class Debuffed(val stat: Stat, val debuffAmount: Int, val turnsRemaining: Int) :
        StatusEffect()
}

sealed class Stat {
    object Health : Stat()
    object Strength : Stat()
    object Defense : Stat()
    object Speed : Stat()
    object Mind : Stat()
}

val attack = Ability.Offensive("Attack", DamageType.Physical(1f), cost = 0)


val abilityList = listOf(
    Ability.Offensive("Fireball", DamageType.Fire(3f,.5f),5),
    Ability.Heal("Heal", 50,5),
    Ability.Buff("Strength Buff", Stat.Strength, 10,5),
    Ability.Debuff("Poison", Stat.Health, 5,5),
    Ability.Taunt("Taunt",0),
    Ability.Stealth("Stealth",0)
)