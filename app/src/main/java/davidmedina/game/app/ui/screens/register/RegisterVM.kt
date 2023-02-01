package davidmedina.game.app.ui.screens.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import davidmedina.game.app.data.repository.MetaGameRepository
import kotlinx.coroutines.launch

data class RegisterState(
  val userName: String = "",
  val loading: Boolean = false,
  val showSaved: Boolean = false,
  val error: String? = null
)

class RegisterViewModel(val gameRepository: MetaGameRepository) : ViewModel() {


  var uiState by mutableStateOf(RegisterState())
    private set

  fun updateUserName(userName: String) {

    viewModelScope.launch {
      uiState = uiState.copy(userName = userName)
    }
  }

  fun saveUserName() {
    viewModelScope.launch {
      uiState = uiState.copy(loading = true)
      uiState = try {
        gameRepository.register(uiState.userName)
        uiState.copy(loading = false, showSaved = true)
      } catch (e: Throwable) {
        uiState.copy(loading = false, error = e.toString())
      }
    }
  }

  fun dismissSaved() {
    uiState = uiState.copy(showSaved = false)

  }

}
