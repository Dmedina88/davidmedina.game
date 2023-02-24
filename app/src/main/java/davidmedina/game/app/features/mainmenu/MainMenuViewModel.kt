package davidmedina.game.app.features.mainmenu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import davidmedina.game.app.data.models.MetaGameState
import davidmedina.game.app.data.repository.MetaGameRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


data class State(
    val gameState: MetaGameState? = null,
    val debugMode: Boolean = false,
)

class MainMenuViewModel(gameRepository: MetaGameRepository) : ViewModel() {


    var uiState by mutableStateOf(State())
        private set

    private val passCode = listOf('T', 'T', 'M', 'D', 'A')
    private val userInput = mutableListOf<Char>()

    init {
        viewModelScope.launch {
            gameRepository.getGameState().collectLatest {
                uiState = uiState.copy(gameState = it)
            }
        }
    }


    fun onTitleClicked(char: Char) {
        userInput.add(char)
        val passCodePart = passCode.subList(0, userInput.size)
        if (userInput == passCodePart) {
            if (userInput.size == passCode.size) {
                uiState = uiState.copy(debugMode = true)
            }
        } else {
            userInput.clear()
        }
    }


}
