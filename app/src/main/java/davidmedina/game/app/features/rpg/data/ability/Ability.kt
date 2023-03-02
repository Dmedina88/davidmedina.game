package davidmedina.game.app.features.rpg.data.ability;

sealed class Ability(open val name: String, open val cost: Int = 0) {
    data class Offensive(
        override val name: String,
        val damageType: DamageType,
        val damageFacter: Float, // multipy damge of this facter
        val damageStat: Stat, // state used for damge
        override val cost: Int,
        val effect: StatusEffect? = null
    ) :
        Ability(name, cost)

    data class Heal(val healStat: Stat,override val name: String, val factor: Float, override val cost: Int) :
        Ability(name, cost)

    data class Buff(
        override val name: String,
        val damageType: DamageType,
        val effect: StatusEffect,
        override val cost: Int
    ) : Ability(name, cost)


    // self targeting ablity that gives the agro status effect
    data class Taunt(val agro: Int, override val name: String, override val cost: Int) :
        Ability(name, cost)

    // steath statues effect slower but less agrro
    data class Stealth(override val name: String, override val cost: Int) : Ability(name, cost)
}


sealed class StatusEffect {
    object Poisoned : StatusEffect() //damage over time?
    object Burned : StatusEffect() // damage over time?
    object Frozen : StatusEffect() // incress will cost of ablity
    data class Stunned(val turnsRemaining: Int) : StatusEffect()
    data class Buffed(val stat: Stat, val buffAmount: Int, val turnsRemaining: Int) : StatusEffect()
    data class Debuffed(val stat: Stat, val debuffAmount: Int, val turnsRemaining: Int) : StatusEffect()
}

sealed class Stat {
    object Health : Stat()
    object Strength : Stat()
    object Defense : Stat()
    object Speed : Stat()
    object Mind : Stat()
}

sealed class DamageType {
    object Physical : DamageType()
    object Dream : DamageType()
    object Magical : DamageType()
    object Psychic : DamageType()
}

val attack = Ability.Offensive(
    "Attack", DamageType.Physical, cost = 0,
    damageFacter = 1f, damageStat = Stat.Strength
)


val abilityList = listOf(
    Ability.Offensive("Fireball", DamageType.Magical, 5F, Stat.Strength, 5),
    Ability.Heal(Stat.Mind,"Heal", 50F, 5),
    Ability.Taunt(0, "tount", 0),
    Ability.Stealth("Stealth", 0)
)