package davidmedina.game.app.features.rpg.data

sealed class Items(val expPoints: Int) {
    class Potion(val healAmount: Int = 50) : Items(10)
}
