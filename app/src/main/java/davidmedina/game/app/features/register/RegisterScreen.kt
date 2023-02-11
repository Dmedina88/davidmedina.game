package davidmedina.game.app.features.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onLogin: () -> Unit, vm: RegisterViewModel = koinViewModel()) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Login")
        val focusManager = LocalFocusManager.current
        val state = vm.uiState

        val modifier = Modifier
            .padding(16.dp)
            .width(200.dp)


        OutlinedTextField(
            modifier = modifier,
            value = state.userName,
            onValueChange = vm::updateUserName,
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            label = { Text("username") },
        )


        Button(onClick = {
            vm.saveUserName()
            focusManager.clearFocus()

        }) {
            Text(text = "Save Name")
        }

        Button(onClick = onLogin) {
            Text(text = "Skip")
        }

        if (state.showSaved) {
            val context = LocalContext.current
            Toast.makeText(context, "Name Saved", Toast.LENGTH_LONG).show()
            vm.dismissSaved()
        }
    }
}
