package davidmedina.game.app.data.models

import davidmedina.game.app.Routes
import davidmedina.game.app.features.rpg.Character
import java.util.*

data class MetaGameState(
    val userName: String = "",
    val id: UUID? = null,
    val screensVisited: Map<Routes, Int> = emptyMap(),
    val rpgCharacter :  List<Character>,
    val rpgItems :  List<Items>
)

//data class ScreenVisited(val routes: Routes, val count : Int)




sealed class Items(val expPoints :Int) {
    object Potion : Items(10)
}

