package davidmedina.game.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun Color.shift(shift: Float): Color {
    val hsv = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsv)
    hsv[2] *= 1.0f + shift
    return Color(ColorUtils.HSLToColor(hsv))
}
