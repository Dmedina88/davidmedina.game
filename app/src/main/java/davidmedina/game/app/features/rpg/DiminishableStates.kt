package davidmedina.game.app.features.rpg

data class DiminishableStates(val current: Int, val max: Int)

fun DiminishableStates.increaseMax(increment: Int): DiminishableStates {
    val newMax = max + increment
    return copy(current = newMax, max = newMax)
}

val DiminishableStates.percentage: Float get() = current.toFloat() / max

val DiminishableStates.states: String get() =
     "$current / $max"


