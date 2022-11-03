package davidmedina.game.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import davidmedina.game.app.viewmodel.ActionComposerState



//todo maybe have on click return an updateed action?
@Composable
fun ActionOverlay(action : ActionComposerState, onTargetSelected: (Int) -> Unit, confirm: (() -> Unit)? = null )  {

    Box(modifier = Modifier.fillMaxSize()) {
        when (action) {
            is ActionComposerState.PlayCard -> PlayCardOverlay(action,onTargetSelected)
            is ActionComposerState.AttackAction -> Box() {}
        }
    }

}

@Composable
private fun PlayCardOverlay( action :ActionComposerState.PlayCard,onClick: (Int) -> Unit ){
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

            .width(150.dp)
            .height(200.dp)
    )
}