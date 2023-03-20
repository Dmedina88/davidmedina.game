package davidmedina.game.app.features.ai_musemum.tactical

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch




val sampleTiles = List(10) { rowIndex ->
    List(10) { colIndex ->
        when {
            rowIndex == 0 && colIndex == 0 -> Tile(TileType.GRASS, TacticalCharacter("Alice", CharacterId.ALICE, Team.ALLY, 10, 10, 10, 10))
            rowIndex == 9 && colIndex == 9 -> Tile(TileType.GRASS, TacticalCharacter("Enemy1", CharacterId.DEFAULT, Team.ENEMY, 8, 8, 8, 8))
            rowIndex == 9 && colIndex == 0 -> Tile(TileType.GRASS, TacticalCharacter("Enemy2", CharacterId.DEFAULT, Team.ENEMY, 8, 8, 8, 8))
            rowIndex == 0 && colIndex == 9 -> Tile(TileType.GRASS, TacticalCharacter("Enemy3", CharacterId.DEFAULT, Team.ENEMY, 8, 8, 8, 8))
            rowIndex == 1 && colIndex == 1 -> Tile(TileType.GRASS, TacticalCharacter("Enemy3", CharacterId.DEFAULT, Team.ENEMY, 8, 8, 8, 8))

            else -> Tile(TileType.GRASS, null)
        }
    }
}

enum class GameMode {
    IDLE,
    MOVE,
    ATTACK,
    USE_SKILL
}

class GameViewModel : ViewModel() {

    private val _tiles = MutableStateFlow(sampleTiles)
    val tiles: StateFlow<List<List<Tile>>> = _tiles.asStateFlow()

    private val _selectedCharacter =
        MutableStateFlow<Pair<TacticalCharacter, Pair<Int, Int>>?>(null)
    val selectedCharacter: StateFlow<Pair<TacticalCharacter, Pair<Int, Int>>?> =
        _selectedCharacter.asStateFlow()

    private val _turn = MutableStateFlow(0)
    val turn: StateFlow<Int> = _turn.asStateFlow()


    // Replace the _moveMode property with _gameMode
    private val _gameMode = MutableStateFlow(GameMode.IDLE)
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()


    init {
        _tiles.onEach { newValue ->
            println("Tiles updated: $newValue")
        }.launchIn(viewModelScope)

        _selectedCharacter.onEach { newValue ->
            println("Selected character updated: $newValue")
        }.launchIn(viewModelScope)


        // Add logging for _gameMode
        _gameMode.onEach { newValue ->
            println("Game mode updated: $newValue")
        }.launchIn(viewModelScope)
    }

    fun useSkill(skillIndex: Int, targetRow: Int, targetCol: Int) {
        val selectedChar = _selectedCharacter.value?.first ?: return
        val targetTile = _tiles.value[targetRow][targetCol]

        if (targetTile.character != null) {
            val (newUser, newTarget) = selectedChar.useSkill(
                selectedChar.skills[skillIndex],
                targetTile.character
            )
            _tiles.update { tiles ->
                tiles.mapIndexed { rowIdx, row ->
                    row.mapIndexed { colIdx, tile ->
                        when {
                            rowIdx == targetRow && colIdx == targetCol -> tile.copy(character = newTarget)
                            else -> tile
                        }
                    }
                }
            }
            updateCharacter(newUser)
        }
    }

    private fun updateCharacter(updatedCharacter: TacticalCharacter) {
        _tiles.update { tiles ->
            tiles.mapIndexed { rowIdx, row ->
                row.mapIndexed { colIdx, tile ->
                    if (tile.character?.id == updatedCharacter.id) {
                        tile.copy(character = updatedCharacter)
                    } else {
                        tile
                    }
                }
            }
        }
    }


    fun enterMoveMode() {
        _gameMode.value = GameMode.MOVE
    }

    // And when exiting move mode
    fun exitMoveMode() {
        _gameMode.value = GameMode.IDLE
    }

    fun performAction(action: suspend () -> Unit) {
        viewModelScope.launch() {
            action()
            delay(1000) // Optional delay between turns
            _turn.value = (_turn.value + 1) % _tiles.value.flatten().count { it.character != null }
        }
    }

    fun handleTileClick(tile: Tile, rowIndex: Int, colIndex: Int) {
        println("handleTileClick called with tile: $tile, rowIndex: $rowIndex, colIndex: $colIndex, gameMode: ${_gameMode.value}")

        when (_gameMode.value) {
            GameMode.IDLE -> {
                if (tile.character != null) {
                    if (_selectedCharacter.value?.second != Pair(rowIndex, colIndex)) {
                        _selectedCharacter.value = Pair(tile.character, Pair(rowIndex, colIndex))
                        println("Character selected: ${_selectedCharacter.value}")
                    }
                }
            }
            GameMode.MOVE -> {
                if (tile.character == null && _selectedCharacter.value != null) {
                    val (selectedChar, selectedPos) = _selectedCharacter.value!!

                    _tiles.update {
                        it.mapIndexed { rowIdx, row ->
                            row.mapIndexed { colIdx, tile ->
                                when {
                                    rowIdx == selectedPos.first && colIdx == selectedPos.second -> tile.copy(
                                        character = null
                                    )
                                    rowIdx == rowIndex && colIdx == colIndex -> tile.copy(character = selectedChar)
                                    else -> tile
                                }
                            }
                        }
                    }
                    _selectedCharacter.value = null
                    exitMoveMode()
                    println("Character moved to new position: $rowIndex, $colIndex")
                }
            }
            GameMode.ATTACK -> {
                // Handle attack action here
            }
            GameMode.USE_SKILL -> {
                // Handle use skill action here
            }
        }
    }


    // Add a new property to store the skill target positions
    private val _skillTargets = MutableStateFlow<List<Pair<Int, Int>>>(listOf())
    val skillTargets: StateFlow<List<Pair<Int, Int>>> = _skillTargets.asStateFlow()

    // Add a new function to enter skill usage mode and set the skill targets
    fun enterSkillUsageMode(skill: Skill) {
        _gameMode.value = GameMode.USE_SKILL
        val selectedChar = _selectedCharacter.value?.first ?: return
        val selectedPos = _selectedCharacter.value?.second ?: return
        val aoe = skill.aoeSize


        // Calculate the skill target positions based on the AOE range
        val targets = mutableListOf<Pair<Int, Int>>()
        for (rowOffset in -aoe..aoe) {
            for (colOffset in -aoe..aoe) {
                val newRow = selectedPos.first + rowOffset
                val newCol = selectedPos.second + colOffset
                if (newRow >= 0 && newRow < _tiles.value.size &&
                    newCol >= 0 && newCol < _tiles.value[newRow].size
                ) {
                    targets.add(Pair(newRow, newCol))
                }
            }
        }
        _skillTargets.value = targets
    }

    // Add a new function to confirm the skill usage and apply the effect
    fun confirmSkillUsage(skill: Skill, targetRow: Int, targetCol: Int) {
        val selectedChar = _selectedCharacter.value?.first ?: return
        val targetTile = _tiles.value[targetRow][targetCol]

        if (targetTile.character != null) {
            val (newUser, newTarget) = skill.effect(selectedChar, targetTile.character)
            _tiles.update { tiles ->
                tiles.mapIndexed { rowIdx, row ->
                    row.mapIndexed { colIdx, tile ->
                        when {
                            rowIdx == targetRow && colIdx == targetCol -> tile.copy(character = newTarget)
                            else -> tile
                        }
                    }
                }
            }
            updateCharacter(newUser)
        }
        // Exit the skill usage mode and clear the skill targets
        _gameMode.value = GameMode.IDLE
        _skillTargets.value = listOf()
    }

}

