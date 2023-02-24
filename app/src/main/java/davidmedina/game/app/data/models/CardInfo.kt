package davidmedina.game.app.data.models

data class CardInfo(
    val name: String,
    val lore: String,
    val life: Int,
    val power: Int,
    val actions: List<CardAction>
)


sealed class CardAction(val cost: Int) {
    data class Attack(val damage: Int) : CardAction(1)
    data class DirectAttack(val damage: Int) : CardAction(1)
    data class Heal(val damage: Int) : CardAction(1)
}