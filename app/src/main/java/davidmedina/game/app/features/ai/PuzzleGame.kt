package davidmedina.game.app.features.ai


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun PuzzleGame() {
    // Define a list of colors for the boxes
    val colors = remember {
        listOf(
            Color.Red,
            Color.Green,
            Color.Blue,
            Color.Yellow,
            Color.Magenta,
            Color.Cyan
        )
    }
// Define the secret code for the puzzle
    val secretCode = remember { mutableStateListOf(0, 1, 2, 3, 1, 2) }

// Define the current state of the puzzle
    val puzzleState = remember { mutableStateListOf(0, 1, 2, 3, 1, 3) }

// Check if the puzzle has been solved
    val puzzleSolved = puzzleState.toList() == secretCode.toList()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Puzzle Game",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(colors) { index, color ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(color = colors[puzzleState[index]])
                        .clickable {
                            if (!puzzleSolved) {
                                puzzleState[index] = (puzzleState[index] + 1) % colors.size
                            }
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (puzzleSolved) {
            Text(
                text = "Puzzle solved!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
        }
    }
}


@Preview
@Composable
fun Puzzle() {
    var slider1Position by remember { mutableStateOf(0f) }
    var slider2Position by remember { mutableStateOf(0f) }
    var toggle1State by remember { mutableStateOf(false) }
    var toggle2State by remember { mutableStateOf(false) }
    var toggle3State by remember { mutableStateOf(false) }
    var switch1State by remember { mutableStateOf(false) }
    var switch2State by remember { mutableStateOf(false) }
    var switch3State by remember { mutableStateOf(false) }
    var puzzleSolved by remember { mutableStateOf(false) }

    puzzleSolved = slider1Position == 0f && slider2Position == 100f &&
            !toggle1State && toggle2State && toggle3State &&
            switch1State && switch2State && !switch3State

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Puzzle",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Slider(
            value = slider1Position,
            onValueChange = { slider1Position = it },
            valueRange = 0f..100f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = slider2Position,
            onValueChange = { slider2Position = it },
            valueRange = 0f..100f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FilledIconToggleButton(
            checked = toggle1State,
            onCheckedChange = { toggle1State = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) { Text("1") }

        Spacer(modifier = Modifier.height(8.dp))

        FilledIconToggleButton(
            checked = toggle2State,
            onCheckedChange = { toggle2State = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("2")
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilledIconToggleButton(
            checked = toggle3State,
            onCheckedChange = { toggle3State = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("3")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Switch(
            checked = switch1State,
            onCheckedChange = { switch1State = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Switch(
            checked = switch2State,
            onCheckedChange = { switch2State = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Switch(
            checked = switch3State,
            onCheckedChange = { switch3State = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (puzzleSolved) {
            Text(
                text = "Puzzle Solved!",
                fontSize = 24.sp,
                color = Color.Green,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
