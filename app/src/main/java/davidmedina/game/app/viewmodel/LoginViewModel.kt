package davidmedina.game.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import davidmedina.game.app.data.repository.LoginRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

data class LoginState(
  val userName: String = "",
  val password: String = "",
  val isLoading: Boolean = false,
  val logInNavPending: Boolean = false
)

class LoginViewModel (val loginRepository: LoginRepository) : ViewModel() {


  var uiState by mutableStateOf(LoginState())
  private set

  fun updateUserName(userName: String) {

    viewModelScope.launch {
      uiState = uiState.copy(userName = userName)
    }
  }

  fun updatePassword(password: String) {
    viewModelScope.launch {
      uiState = uiState.copy(password = password)
    }
  }

  fun login() {

    uiState = uiState.copy(isLoading = true)
    viewModelScope.launch {
      delay(3000)
      uiState = uiState.copy(isLoading = false, logInNavPending = true)

    }
  }

  fun navigated() {
    uiState = uiState.copy(logInNavPending = false)
  }

  override fun onCleared() {
    Timber.i("onCleared")
    super.onCleared()
  }
}
