package davidmedina.game.app.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

fun DrawScope.drawGrid(){

    var posX = 0F
    while (posX <= size.width) {
        drawLine(Color.Blue, Offset(posX, 0F), (Offset(posX, size.height)))
        posX += 100
    }

    var posY = 0F
    while (posY < size.height) {
        drawLine(Color.Blue, Offset(0F, posY), (Offset(size.width, posY)))
        posY += 100
    }
}