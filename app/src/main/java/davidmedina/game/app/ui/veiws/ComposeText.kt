package davidmedina.game.app.ui.veiws

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PasswordText(modifier : Modifier = Modifier ,password : String, onTextChange : (String) -> Unit){
  var passwordVisibility by remember { mutableStateOf(false) }

  OutlinedTextField(
    modifier =  modifier,
    value = password,
    placeholder = { Text("Password") },
    onValueChange = onTextChange,
    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
    label = { Text("Password") },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    trailingIcon = {
      val image = if (passwordVisibility)
        Icons.Filled.Visibility
      else Icons.Filled.VisibilityOff

      IconButton(onClick = {
        passwordVisibility = !passwordVisibility
      }) {
        Icon(imageVector  = image, "")
      }
    }
  )


}