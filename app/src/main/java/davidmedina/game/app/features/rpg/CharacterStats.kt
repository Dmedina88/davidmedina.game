package davidmedina.game.app.features.rpg

data class CharacterStats(
    val name: String,
    val characterID: CharacterID,
    val Hp: DiminishableStates,
    val strength: Int,
    val defense: Int
)

data class DiminishableStates(val current: Int, val max: Int)

val mockCharicter =
    CharacterStats("BlueOger", CharacterID.BLUE_OGER, DiminishableStates(20, 20), 10, 10)


enum class CharacterID {
    BLUE_OGER,
    OTHER_OGER,
}


