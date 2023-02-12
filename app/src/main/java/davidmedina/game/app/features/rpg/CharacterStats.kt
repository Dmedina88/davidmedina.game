package davidmedina.game.app.features.rpg

import davidmedina.game.app.R

data class CharacterStats(
    val name: String,
    val characterID: CharacterId,
    val Hp: DiminishableStates,
    val Will: DiminishableStates,
    val strength: Int,
    val defense: Int,
    val speed: Int
)

data class DiminishableStates(val current: Int, val max: Int){

    override fun toString(): String {
        return "$current / $max"
    }
}


enum class CharacterId {
    BLUE_OGER,
    OTHER_OGER,
}

fun CharacterId.createCharacter(name: String? = null) = when (this) {
    CharacterId.BLUE_OGER -> CharacterStats(
        name ?: "BlueOger",
        CharacterId.BLUE_OGER,
        DiminishableStates(20, 20),
        DiminishableStates(20, 20),
        10,
        10,
        10
    )
    CharacterId.OTHER_OGER -> CharacterStats(
        name ?: "Othger",
        CharacterId.OTHER_OGER,
        DiminishableStates(20, 20),
        DiminishableStates(20, 20),
        10,
        10,
        10
    )
}

 val CharacterId.battleImage: Int
    get() = when (this){
        CharacterId.BLUE_OGER -> R.drawable.blue_oger_portrite
        CharacterId.OTHER_OGER -> R.drawable.other_oger
    }


