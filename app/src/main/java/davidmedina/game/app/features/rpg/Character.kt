package davidmedina.game.app.features.rpg

import davidmedina.game.app.R
import davidmedina.game.app.data.models.Items
import kotlin.math.max

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
) {
    val isAlive get() = hp.current > 0
    val damage get() = strength / 4
}



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

// Extension function to calculate new stats after leveling up
private fun Character.levelUp(newExp : Int): Character {
    return Character(
        name = name,
        characterID = characterID,
        hp = hp.increaseMax(10),
        will = will.increaseMax(10),
        strength = strength + 2,
        defense = defense + 2,
        speed = speed + 0.1f,
        mind = mind + 2,
        exp = newExp,
        level = level +1
    )
}
enum class CharacterId {
    BLUE_OGER,
    OTHER_OGER,
    Berserker,
    Wizard,
    Paladin
}

fun CharacterId.createCharacter(name: String? = null) = when (this) {
    CharacterId.BLUE_OGER -> Character(
        name ?: "BlueOger",
        CharacterId.BLUE_OGER,
        DiminishableStates(20, 20),
        DiminishableStates(15, 20),
        16,
        10,
        .03f,
        10
    )

    CharacterId.OTHER_OGER -> Character(
        name ?: "Othger",
        CharacterId.OTHER_OGER,
        DiminishableStates(20, 20),
        DiminishableStates(20, 20),
        10,
        10,
        .03f,
        10
    )
    CharacterId.Berserker -> TODO()
    CharacterId.Wizard -> TODO()
    CharacterId.Paladin -> TODO()
}

val CharacterId.battleImage: Int
    get() = when (this) {
        CharacterId.BLUE_OGER -> R.drawable.blue_oger_portrite
        CharacterId.OTHER_OGER -> R.drawable.other_oger
        else -> {R.drawable.gen_land_trait_apple_tree}
    }


//these will mape to fuctions on how to do the damge// think battle actions
sealed class Ability(open val name: String) {
     data class Offensive< T: DamageType>(override val name: String,  val damageType : T) : Ability(name)
}

val attack = Ability.Offensive("Attack",DamageType.Physical(1f))

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


//classes of damage Convay Damige type
sealed class DamageType() {
    data class Physical( val damageFactor: Float) : DamageType()
    data class Dream(val damageFactor: Float) : DamageType()
    data class Posion( val damageFactor: Float,val procValue: Float) : DamageType()
}


