package davidmedina.game.app.features.ai_musemum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun AIUIWorkCarousel() {
    Carousel(
        listOf(
            { Puzzle() },
            { PuzzleGame() },
            { RandomShapeScreen() },
            { ShapeScreen() },
            { Hallway() },
            { AIWordMap1() },
            { ArtBox_AI() },
            { MapWithGeographicalFeatures() }
        )
    )
}

@Composable
fun Carousel(items: List<@Composable () -> Unit>, modifier: Modifier = Modifier) {
    var currentIndex by remember { mutableStateOf(0) }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { currentIndex = (currentIndex - 1).coerceAtLeast(0) },
            enabled = currentIndex > 0
        ) {
            AnimatedVisibility(currentIndex != 0) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

        }
        Box(modifier = Modifier.weight(1f)) {
            items[currentIndex]()
        }
        IconButton(
            onClick = { currentIndex = (currentIndex + 1).coerceAtMost(items.size - 1) },
            enabled = currentIndex < items.size - 1
        ) {
            AnimatedVisibility(currentIndex != items.lastIndex) {

                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        items.forEachIndexed { index, _ ->
            val color =
                if (index == currentIndex) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.4f
                )
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { currentIndex = index }
            )
        }
    }
}
