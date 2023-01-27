package davidmedina.game.app.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp


fun Modifier.resizeWithOffset(width: Dp, height: Dp, x: Dp, y: Dp) = this.then(
    this
        .height(height)
        .width(width)
        .offset(x = x, y = y)
)

fun Modifier.resizeWithCenterOffset(width: Dp, height: Dp, x: Dp, y: Dp) =
    resizeWithOffset(width, height, x = x - width.div(2), y = y - height.div(2))


