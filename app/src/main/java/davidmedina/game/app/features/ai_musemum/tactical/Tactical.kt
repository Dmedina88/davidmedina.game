package davidmedina.game.app.features.ai_musemum.tactical

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import davidmedina.game.app.R
import davidmedina.game.app.features.rpg.battle.ui.CharacterInfo
import davidmedina.game.app.ui.composables.gameBoxBackground


// An enum class to represent the tile types
enum class TileType {
    // Define the possible values for the tile types
    GRASS,
    WATER,
    DEFAULT
}

// A data class to represent a tile on the map
data class Tile(
    // Change the type of the tile type property to TileType
    val type: TileType,
    // The character property is unchanged from before
    val character: TacticalCharacter?
)

// A composable function to display a tile
@Composable
fun TileView(
    tile: Tile,
    onTileClick: () -> Unit,
    isSkillTarget: Boolean = false, // Add this parameter, with a default value of false
    onSkillTargetClick: (() -> Unit)? = null // Add this parameter, with a default value of null
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(4.dp))
    ) {
        // Use a when expression to set the background color based on the tile type
        when (tile.type) {
            TileType.GRASS -> Color(0xFF00FF00)
            TileType.WATER -> Color(0xFF0000FF)
            TileType.DEFAULT -> Color(0xFF808080)
        }.let { color ->
            // Use the let function to set the background color
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .clickable(onClick = onTileClick)
            )
        }
        if (tile.character != null) {
            Image(
                painter = painterResource(id = tile.character.toImageResource()),
                contentDescription = "Character",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Add a transparent layer to indicate skill target positions
        if (isSkillTarget) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000)) // Semi-transparent black color
                    .clickable(onClick = onSkillTargetClick ?: {})
            )
        }
    }
}


// Add a new composable function for the action menu
@Composable
fun ActionMenu(
    selectedCharacter: TacticalCharacter?,
    onMove: () -> Unit,
    onAttack: () -> Unit,
    onUseSkill: (Skill) -> Unit
) {
    // Define a state for showing/hiding the popover
    val showPopover = remember { mutableStateOf(false) }

    Row() {
        Column(
            Modifier
                .gameBoxBackground()
        ) {
            Button(
                onClick = onMove,
            ) {
                Text(text = "Move")
            }

            Button(
                onClick = onAttack,
            ) {
                Text(text = "Attack")
            }

            // Add a Button for the "Skill" option, which toggles the showPopover state
            Button(
                onClick = {
                    showPopover.value = !showPopover.value
                },
            ) {
                Text(text = "Skill")
            }
        }

        // If showPopover state is true, show the popover
        if (showPopover.value && selectedCharacter != null) {
            SkillPopover(showPopover, selectedCharacter, onUseSkill)
        }
    }
}


@Preview
@Composable
fun BattleMapView() {
    val viewModel: GameViewModel by remember { mutableStateOf(GameViewModel()) }
    val tiles by viewModel.tiles.collectAsState()
    val selectedCharacter by viewModel.selectedCharacter.collectAsState()
    val turn by viewModel.turn.collectAsState()
    val gameMode by viewModel.gameMode.collectAsState()
    val skillTargets by viewModel.skillTargets.collectAsState()

    Column {
        Text(text = "Turn: $turn", fontSize = 24.sp, modifier = Modifier.padding(8.dp))

        // Add the action menu for the selected character
        AnimatedVisibility(selectedCharacter?.first != null) {

            ActionMenu(
                selectedCharacter = selectedCharacter?.first,
                onMove = { viewModel.enterMoveMode() },
                onAttack = { /* Handle attack action here */ },
                onUseSkill = { viewModel.enterSkillUsageMode(it) }
            )
        }

        // Use a for loop to iterate through the rows of the tiles
        tiles.forEachIndexed { rowIndex, row ->
            // Use a Row composable to display the tiles in a horizontal list
            Row {
                row.forEachIndexed { colIndex, tile ->
                    val isSkillTarget =
                        gameMode == GameMode.USE_SKILL && Pair(rowIndex, colIndex) in skillTargets
                    TileView(
                        tile = tile,
                        onTileClick = { viewModel.handleTileClick(tile, rowIndex, colIndex) },
                        isSkillTarget = isSkillTarget, // Pass this value to the TileView composable
                        onSkillTargetClick = {
                            viewModel.confirmSkillUsage(warriorSkill1, rowIndex, colIndex)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun SkillPopover(
    showPopover: MutableState<Boolean>,
    selectedCharacter: TacticalCharacter,
    onSkillSelected: (Skill) -> Unit
) {
    Column(
        modifier = Modifier
            .gameBoxBackground()
            .wrapContentSize()
    ) {
        Text(text = "${selectedCharacter.name}'s Skills:")
        selectedCharacter.skills.forEach { skill ->
            Button(
                onClick = {
                    onSkillSelected(skill)
                    showPopover.value = false
                },
            ) {
                Text(text = skill.name)
            }
        }
    }
}
