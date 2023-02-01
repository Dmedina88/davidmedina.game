package davidmedina.game.app.ui.screens.cardgame

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import davidmedina.game.app.ui.theme.playingCardSize


@Composable
fun ActionOverlay(action : ActionComposerState, onTargetSelected: (Int) -> Unit, cansel: (() -> Unit) = {}, confirm: (() -> Unit) = {} )  {

    Row(modifier = Modifier.fillMaxSize()) {

            when (action) {
                is ActionComposerState.PlayCard -> PlayCardOverlay(action, onTargetSelected)
                is ActionComposerState.AttackAction -> Box() {}
                else -> {}
            }

        Button(modifier = Modifier.playingCardSize(),
            onClick = {cansel()}) {
            Text(text = "cansel")
        }
        Button(modifier = Modifier.playingCardSize(),
            onClick = {confirm()}) {
            Text(text = "confirm")
        }
    }

}

@Composable
private fun PlayCardOverlay(action : ActionComposerState.PlayCard, onClick: (Int) -> Unit ){

       Column() {
           LazyRow {
               itemsIndexed(items = action.validOponenteFields) { index, valid ->
                   SelectableField(valid = valid, selected = action.field == index) {
                       onClick(index)
                   }
               }
           }

           LazyRow {
               itemsIndexed(items = action.validFields) { index, valid ->
                   SelectableField(valid = valid, selected = action.field == index) {
                       onClick(index)
                   }
               }
           }

       }

}

@Composable
fun SelectableField(valid: Boolean, selected: Boolean, onClick: () -> Unit) {

    Box(
        Modifier
            .clickable(enabled = valid) { onClick() }
            .background(
                if (!valid) Color.Magenta.copy(alpha = 0.4f) else
                    if (!selected) Color.Red.copy(alpha = 0.4f) else Color.Yellow.copy(alpha = 0.6f)
            )
            .playingCardSize()

    )
}