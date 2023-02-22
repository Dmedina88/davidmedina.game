package davidmedina.game.app.features.rpg

import davidmedina.game.app.R
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
    val exp: Int =0,
    val level: Int =0,
    ) {
    val isAlive get() = hp.current > 0
    val damage get() = strength.div(4)
}


data class DiminishableStates(val current: Int, val max: Int) {

    override fun toString(): String {
        return "$current / $max"
    }

    val percentage: Float get() = current.toFloat() / max
}


enum class CharacterId {
    BLUE_OGER,
    OTHER_OGER,
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
}

val CharacterId.battleImage: Int
    get() = when (this) {
        CharacterId.BLUE_OGER -> R.drawable.blue_oger_portrite
        CharacterId.OTHER_OGER -> R.drawable.other_oger
    }


//these will mape to fuctions on how to do the damge// think battle actions
sealed class Ability(open val name: String) {
     data class Offensive< T: DamageType>(override val name: String,  val damageType : T) : Ability(name)

}

val attack = Ability.Offensive("Attack",DamageType.Physical(1f))

fun Character.takeDamage(damageType: DamageType, damageValue : Int): Character {

    //cacuilate resistinces
    val finalValue : Int = damageValue
    //check for status effects

    val newHp = max(hp.current - finalValue, 0)

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


