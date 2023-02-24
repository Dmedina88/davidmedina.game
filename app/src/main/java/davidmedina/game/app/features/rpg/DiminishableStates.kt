package davidmedina.game.app.features.rpg



data class DiminishableStates(
    var max: Int,
    var current: Int = max
)


fun DiminishableStates.increaseMax(increment: Int): DiminishableStates {
    val newMax = max + increment
    return copy(current = newMax, max = newMax)
}

val DiminishableStates.percentage: Float get() = current.toFloat() / max

val DiminishableStates.battleText: String get() =
     "$current / $max"


