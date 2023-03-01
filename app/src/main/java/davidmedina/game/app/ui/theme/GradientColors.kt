package davidmedina.game.app.ui.theme

import androidx.compose.ui.graphics.Color


sealed class GradientColors(val colors: List<Color>) {
    object RedGradient : GradientColors(listOf(Color(0xFFFF9966), Color(0xFFFF5E62)))
    object BlueGradient : GradientColors(listOf(Color(0xFF5CA8FF), Color(0xFFE68AFF)))
    object GreenGradient : GradientColors(listOf(Color(0xFF12B591), Color(0xFF8EDD8A)))
    object YellowGradient : GradientColors(listOf(Color(0xFFFFF44F), Color(0xFFFFA64D)))
    object GreenToBlueGradient : GradientColors(listOf(Color.Green, Color.Blue))

    // Add a 3-color gradient
    object BlueGreenPurpleGradient : GradientColors(listOf(Color.Blue, Color.Green, Color.Magenta))

    // Add a 4-color gradient
    object RedYellowGreenBlueGradient :
        GradientColors(listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue))

    object PurpleGradient : GradientColors(listOf(Color(0xFFD741A7), Color(0xFF7B2CBF)))
    object OrangeGradient : GradientColors(listOf(Color(0xFFFFA17F), Color(0xFFCC6600)))
    object PinkGradient : GradientColors(listOf(Color(0xFFFF90C3), Color(0xFFFF46B8)))
    object TealGradient : GradientColors(listOf(Color(0xFF00B4D8), Color(0xFF48CAE4)))
    object GrayGradient : GradientColors(listOf(Color(0xFF9BA3A7), Color(0xFF545E6F)))
}