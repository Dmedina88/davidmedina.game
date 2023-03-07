package davidmedina.game.app.features.rpg.map.location

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.features.ai_musemum.Map2
import davidmedina.game.app.features.ai_musemum.PuzzleGame
import davidmedina.game.app.features.ai_musemum.RandomShapeScreen
import davidmedina.game.app.features.ai_musemum.ShapeScreen
import davidmedina.game.app.features.storygame.blueoger.level1.BlueOgerOpening


// navigater preview
@Preview
@Composable
private fun LocationNavigatorPreview() {
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


@Composable
fun LocationNavigatorScreen(locationMap: List<List<@Composable () -> Unit>>) {
    var row by remember { mutableStateOf(0) }
    var column by remember { mutableStateOf(0) }

    locationMap[row][column]()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            Modifier.align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                IconButton(
                    onClick = { if (row > 0) row-- },
                    enabled = row > 0
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "up")
                }
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(
                    onClick = { if (column > 0) column-- },
                    enabled = column > 0
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "left")
                }

                MiniMap(
                    locationMap.size,
                    locationMap[0].size,
                    row, column
                )

                IconButton(
                    onClick = { if (column < locationMap[row].size - 1) column++ },
                    enabled = column < locationMap[row].size - 1
                ) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "right")
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                IconButton(
                    onClick = { if (row < locationMap.size - 1) row++ },
                    enabled = row < locationMap.size - 1
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "down")
                }
            }
        }

    }
}

@Composable
private fun MiniMap(xSize: Int, ySize: Int, currentRow: Int, currentColumn: Int) {
    Grid(
        rows = xSize,
        columns = ySize,
        content = { row, column ->
            Box(
                Modifier
                    .size(16.dp)
                    .background(
                        if (row == currentRow && column == currentColumn) Color.Red
                        else Color.Gray
                    )
            )
        }
    )
}


@Composable
private fun Grid(
    rows: Int,
    columns: Int,
    content: @Composable (row: Int, column: Int) -> Unit
) {

    Column() {
        for (rowIndex in 0 until rows) {
            Row() {
                for (columnIndex in 0 until columns) {
                    content(rowIndex, columnIndex)
                }
            }
        }
    }
}
