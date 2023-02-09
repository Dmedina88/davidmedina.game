package davidmedina.game.app.ui.composables

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TDMTextBox(text: String, onNext: () -> Unit = {}, onFinished: (() -> Unit)? = null) {

    AnimatedVisibility(visible = text.isNotBlank()) {
        val wordList = text.toList()
        val nextText = remember {
            wordList.map { ' ' }.toMutableStateList()
        }
        var textOver by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(key1 = text) {
            wordList.forEachIndexed { index, s ->
                delay(100)
                nextText[index] = s
            }
        }
        Column(
            modifier = Modifier
                .defaultMinSize(minHeight = 200.dp)
                .fillMaxWidth()
                .border(
                    width = 12.dp,
                    brush = Brush.verticalGradient(colors = listOf(Color.Green, Color.Blue)),
                    shape = RoundedCornerShape(percent = 2)
                )
                .clip(shape = RoundedCornerShape(percent = 12))
                .drawBehind {
                    drawRect(Color.Gray)
                }
                .padding(20.dp)
                .noRippleClickable {
                    if (!textOver) {
                        nextText.clear()
                        nextText.addAll(wordList)
                    } else {
                        onNext()
                    }
                },
        ) {


            LazyVerticalGrid(

                // To make the ripple round

                columns = GridCells.Adaptive(20.dp),
                contentPadding = PaddingValues(vertical = 8.dp),

                ) {


                items(nextText.size) { index ->

                    AnimatedContent(
                        targetState = nextText[index],
                        transitionSpec = {
                            slideInVertically { -it } with slideOutVertically { it }
                        }
                    ) { output ->
                        Text(
                            textAlign = TextAlign.Center,
                            text = "$output",
                            fontSize = 32.sp
                        )
                    }

                }

            }
            if (textOver) {
                if (onFinished != null) {
                    onFinished()
                } else {
                    Text(text = "\n NEXT -> ")
                }
            }
        }
        LaunchedEffect(nextText.last()) {
            textOver = if (nextText.last() == wordList.last()) {
                delay(300)
                true
            } else {
                false
            }
        }
    }

}




