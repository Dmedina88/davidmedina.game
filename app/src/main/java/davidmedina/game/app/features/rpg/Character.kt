package davidmedina.game.app.features.rpg

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
    val mutableList: List<StatusEffect> = emptyList()
)

val Character.nextLevel get() = level * 12
val Character.isAlive get() = hp.current > 0
val Character.damage get() = strength

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






fun Character.takeDamage(damageType: DamageType, damageValue : Int): Character {
    //cacuilate resistinces
    val finalValue : Int = damageValue
    val newHp = (hp.current - finalValue).coerceAtLeast(0)
    return copy(
        hp = hp.copy(current = newHp)
    )
}


fun <T: DamageType> Character.performAttack(ability: Ability.Offensive<T>): Int {
    return  max(
        when (ability.damageType) {
            is DamageType.Physical -> (ability.damageType.damageFactor * this.damage).toInt()
            else -> 0
        }, 0
    )


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