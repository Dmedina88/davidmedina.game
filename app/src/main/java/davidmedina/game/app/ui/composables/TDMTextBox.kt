package davidmedina.game.app.ui.composables

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TDMTextBox(text: String ) {

    val wordList = text.split(" ")
    val nextText = remember {
        wordList.map { " " }.toMutableStateList()
    }
    var textOver by remember {
      mutableStateOf(false)
    }

    LaunchedEffect(key1 = text) {
        wordList.forEachIndexed { index, s ->
            delay(300)
            nextText[index] = s
        }
    }
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxHeight(.2f)
            .fillMaxWidth()
            .border(
                width = 12.dp,
                brush = Brush.verticalGradient(colors = listOf(Color.Green, Color.Blue)),
                shape = RoundedCornerShape(percent = 2)
            )
            // To make the ripple round
            .clip(shape = RoundedCornerShape(percent = 12))
            .padding(20.dp)
    ) {


        nextText.forEachIndexed { index, text ->

            AnimatedContent(
                targetState = text,
                transitionSpec = {
                    slideInVertically { -it } with slideOutVertically { it }
                }
            ) { output ->
                Text(
                    text = "$output "
                )
            }


        }

        LaunchedEffect(nextText.last()) {
            textOver = if(nextText.last() == wordList.last()){
                delay(300)
                true
            }else{
                false
            }
        }


        if (textOver) {
            Text(
                text = "\n NEXT -> "
            )
        }

    }
}

