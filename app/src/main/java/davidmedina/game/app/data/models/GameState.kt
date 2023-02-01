package davidmedina.game.app.data.models

import davidmedina.game.app.Routes
import java.util.*

data class MetaGameState(
    val userName: String = "",
    val id: UUID? = null,
    //cards
    //invintory
    //pick flowers
    val screensVisited: Map<Routes, Int> = emptyMap()
)

//data class ScreenVisited(val routes: Routes, val count : Int)




sealed class Items {


}

