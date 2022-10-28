package davidmedina.game.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


data class HomeState(
  val isLoading: Boolean = false,
)

class HomeViewModel : ViewModel() {


  var uiState by mutableStateOf(HomeState())
  private set



}
