package davidmedina.game.app.data.models

class CardInfo


sealed class CardAction(val cost : Int){
    data class Attack(val damage : Int) : CardAction(1)
    data class DirectAttack(val damage : Int) : CardAction(1)
    data class Heal(val damage : Int) : CardAction(1)
}