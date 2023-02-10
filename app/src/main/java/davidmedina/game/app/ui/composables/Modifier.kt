package davidmedina.game.app.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}



fun Modifier.resizeWithOffset(width: Dp, height: Dp, x: Dp, y: Dp) : Modifier {
    var ajustedX = x
    var ajustedY = y
    var offsetX = 0.dp
    var offsetY = 0.dp

    if (x < 0.dp) {
        offsetX = x
        ajustedX =0.dp
    }
    if (y < 0.dp) {
        offsetY = y
        ajustedY =0.dp
    }
    return this.then(
        this
            .padding(start = ajustedX, top = ajustedY)
            .offset(offsetX, offsetY)
            .height(height)
            .width(width)
    )
}


fun Modifier.resizeWithCenterOffset(width: Dp, height: Dp, x: Dp, y: Dp) =
    resizeWithOffset(width, height, x = x - width.div(2), y = y - height.div(2))


