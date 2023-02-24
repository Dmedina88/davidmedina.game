package davidmedina.game.app.features.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import davidmedina.game.app.ui.composables.PasswordText
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogInScreen(onLogin: () -> Unit, loginViewModel: LoginViewModel = koinViewModel()) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Login")

        val state = loginViewModel.uiState

        val modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()

        //CircularProgressIndicator()

        OutlinedTextField(
            modifier = modifier,
            value = state.userName,
            onValueChange = loginViewModel::updateUserName,
            label = { Text("Label") },
        )

        PasswordText(
            modifier = modifier,
            password = state.password,
            onTextChange = loginViewModel::updatePassword
        )


        Button(onClick = loginViewModel::login) {
            Text(text = "login")
        }
        if (state.logInNavPending) {
            onLogin()
        }
    }
}
