package davidmedina.game.app.ui

import androidx.compose.ui.graphics.Color


// i would change this to an inline class if i felt it matter
sealed class GradientColors(val colors: List<Color>) {
    object RedGradient : GradientColors(listOf(Color(0xFFFF9966), Color(0xFFFF5E62)))
    object BlueGradient : GradientColors(listOf(Color(0xFF5CA8FF), Color(0xFFE68AFF)))
    object GreenGradient : GradientColors(listOf(Color(0xFF12B591), Color(0xFF8EDD8A)))
    object YellowGradient : GradientColors(listOf(Color(0xFFFFF44F), Color(0xFFFFDA7A)))
    object GreenToBlueGradient : GradientColors(listOf(Color.Green, Color.Blue))
}