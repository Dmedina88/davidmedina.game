package davidmedina.game.app.features.rpg.data

import davidmedina.game.app.features.rpg.data.ability.Ability
import davidmedina.game.app.features.rpg.data.ability.DamageType
import davidmedina.game.app.features.rpg.data.ability.Stat
import davidmedina.game.app.features.rpg.data.ability.StatusEffect
import kotlin.math.max
import kotlin.random.Random

data class Character(
    val name: String,
    val characterID: CharacterId,
    val hp: DiminishableStates,
    val will: DiminishableStates,
    val strength: Int,
    val defense: Int,
    val speed: Float,
    val mind: Int,
    val exp: Int = 0,
    val level: Int = 1,
    val statusEffect: List<StatusEffect> = emptyList(),
    val ability: List<Ability> = emptyList()
) {
    val Stat.value
        get() = when (this) {
            Stat.Defense -> defense
            Stat.Health -> hp.current
            Stat.Mind -> mind
            Stat.Speed -> speed
            Stat.Strength -> strength
        }.toFloat()
}

val Character.nextLevel get() = level * 12
val Character.isAlive get() = hp.current > 0

// Extension function to calculate experience gained from absorbing an item
fun Character.absorbItem(item: Items): Character {
    val newExp = exp + item.expPoints
    return if (newExp >= level * 10) {
        // Character has leveled up
        this.levelUp(newExp)
    } else {
        this.copy(exp = newExp)
    }
}


fun Character.takeDamage(damageType: DamageType, damageValue: Int): Character {
    //cacuilate resistinces
    val finalValue: Int = damageValue
    val newHp = (hp.current - finalValue).coerceAtLeast(0)
    return copy(
        hp = hp.copy(current = newHp)
    )
}

fun Character.heal(healAmount: Int): Character {
    val newHp = (hp.current + healAmount).coerceAtMost(hp.max)
    return copy(
        hp = hp.copy(current = newHp)
    )
}


fun Character.performAttack(ability: Ability.Offensive): Int {
    return max((ability.damageFacter * ability.damageStat.value).toInt(), 0)
}


fun Character.performHeal(ability: Ability.Heal): Int {
    return max((ability.factor * ability.healStat.value).toInt(), 0)
}

fun createMockCharacters(numCharacters: Int): List<Character> {
    val mockCharacters = mutableListOf<Character>()

    for (i in 1..numCharacters) {
        val characterId = CharacterId.values().random()
        val name = "Mock ${characterId.name} $i"
        val character = characterId.createCharacter(name)

        mockCharacters.add(character.levelUp(Random.nextInt(1000)))
    }

    return mockCharacters
}

 fun Character.addStatusEffect(statusEffect: StatusEffect): Character =
    this.copy(statusEffect = this.statusEffect.toMutableList().plus(statusEffect))

