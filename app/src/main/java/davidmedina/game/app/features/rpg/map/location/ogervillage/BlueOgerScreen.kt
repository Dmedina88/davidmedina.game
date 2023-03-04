package davidmedina.game.app.features.rpg.map.location.ogervillage
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import davidmedina.game.app.features.ai_musemum.Map2
import davidmedina.game.app.features.ai_musemum.PuzzleGame
import davidmedina.game.app.features.ai_musemum.RandomShapeScreen
import davidmedina.game.app.features.ai_musemum.ShapeScreen
import davidmedina.game.app.features.rpg.map.location.LocationNavigatorScreen
import davidmedina.game.app.features.storygame.blueoger.level1.BlueOgerOpening


@Preview
@Composable
private fun BlueOgerScreen() {
    val locationMap = listOf(
        listOf<@Composable () -> Unit>(
            { BlueOgerOpening() },
            { RandomShapeScreen() },
            { Text("Composable 3") }
        ),
        listOf<@Composable () -> Unit>(
            { Map2() },
            { BlueOgerOpening() },
            { Text("Composable 6") }
        ),
        listOf<@Composable () -> Unit>(
            { PuzzleGame() },
            { BlueOgerOpening() },
            { ShapeScreen() }
        )
    )
    LocationNavigatorScreen(locationMap = locationMap)

}