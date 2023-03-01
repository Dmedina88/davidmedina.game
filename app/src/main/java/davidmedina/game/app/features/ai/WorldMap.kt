package davidmedina.game.app.features.ai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.tooling.preview.Preview

data class MapData(
    val size: Size,
    val terrain: List<List<TerrainType>>,
    val landmarks: List<Landmark>
)

data class Landmark(val name: String, val type: LandmarkType, val location: Offset)
enum class TerrainType { Forest, Mountain, River, Ocean, Road }
enum class LandmarkType { Castle, Temple, Dungeon, Village }



@Preview
@Composable
fun AIWordMap1(mapData: MapData? = null) {
    Canvas(modifier = Modifier.fillMaxSize()) {

        val mapData = mapData ?: generateMapData(
            Size(10f, 10f),
            10,
            screenSize = Size(size.width, size.height)
        )
        val tileSize = Size(
            width = size.width / mapData.size.width,
            height = size.height / mapData.size.height
        )
        for (y in 0 until mapData.size.height.toInt()) {
            for (x in 0 until mapData.size.width.toInt()) {
                val terrainType = mapData.terrain[y][x]
                drawTerrain(
                    tileSize = tileSize,
                    terrainType = terrainType,
                    topLeft = Offset(x = x * tileSize.width, y = y * tileSize.height)
                )
            }
        }
        mapData.landmarks.forEach { landmark ->
            drawLandmark(tileSize = tileSize, landmark = landmark)
        }
    }
}

private fun DrawScope.drawTerrain(tileSize: Size, terrainType: TerrainType, topLeft: Offset) {
    val terrainColor = when (terrainType) {
        TerrainType.Forest -> Color(0xFF006400)
        TerrainType.Mountain -> Color(0xFF808080)
        TerrainType.River -> Color(0xFFADD8E6)
        TerrainType.Ocean -> Color(0xFF1E90FF)
        TerrainType.Road -> Color(0xFF8E45F3)

    }
    drawRect(color = terrainColor, topLeft = topLeft, size = tileSize)
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawLandmark(tileSize: Size, landmark: Landmark) {
    val landmarkColor = when (landmark.type) {
        LandmarkType.Castle -> Color(0xFFFFD700)
        LandmarkType.Temple -> Color(0xFF00FF00)
        LandmarkType.Dungeon -> Color(0xFF8B4513)
        LandmarkType.Village -> Color(0xFFfE906F)

    }
    drawCircle(color = landmarkColor, center = landmark.location, radius = tileSize.width / 2)


    /*
    drawText(
        text = landmark.name, color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center,
        topLeft = Offset(
            x = landmark.location.x - tileSize.width / 2,
            y = landmark.location.y - tileSize.height / 2
        )
    )

     */
}

fun generateMapData(size: Size, numLandmarks: Int, screenSize: Size): MapData {
    // Define the list of possible terrain types
    val terrainTypes = TerrainType.values()

    // Initialize the terrain grid with random terrain types
    val terrainGrid =
        MutableList(size.height.toInt()) { MutableList(size.width.toInt()) { terrainTypes.random() } }

    // Add roads to the terrain grid
    val roadFrequency = 0.5f // Increase this value to add more roads
    for (i in 0 until size.width.toInt() - 1) {
        for (j in 0 until size.height.toInt()) {
            if (terrainGrid[j][i] != TerrainType.Road && terrainGrid[j][i + 1] != TerrainType.Road && Math.random() < roadFrequency) {
                terrainGrid[j][i] = TerrainType.Road
            }
        }
    }
    for (i in 0 until size.width.toInt()) {
        for (j in 0 until size.height.toInt() - 1) {
            if (terrainGrid[j][i] != TerrainType.Road && terrainGrid[j + 1][i] != TerrainType.Road && Math.random() < roadFrequency) {
                terrainGrid[j][i] = TerrainType.Road
            }
        }
    }

    // Add landmarks to the map
    val landmarks = mutableListOf<Landmark>()
    val landmarkTypes = listOf(LandmarkType.Castle, LandmarkType.Temple, LandmarkType.Village)
    repeat(numLandmarks) {
        val landmarkType = landmarkTypes.random()
        val x = (0 until screenSize.width.toInt()).random().toFloat() + 0.5f
        val y = (0 until screenSize.height.toInt()).random().toFloat() + 0.5f
        landmarks.add(Landmark("Landmark ${it + 1}", landmarkType, Offset(x, y)))
    }

    // Create the MapData object
    return MapData(size, terrainGrid, landmarks)
}
