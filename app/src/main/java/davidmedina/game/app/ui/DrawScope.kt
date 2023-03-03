package davidmedina.game.app.ui

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

fun DrawScope.drawGrid(
    color: Color = Color.Blue,
    alpha: Float = 1.0f,
    strokeWidth: Float = Stroke.HairlineWidth,
    cap: StrokeCap = Stroke.DefaultCap,
) {
    var posX = 0F
    while (posX <= size.width) {
        drawLine(
            color,
            Offset(posX, 0F),
            Offset(posX, size.height),
            strokeWidth,
            cap,
            alpha = alpha
        )
        posX += 100
    }

    var posY = 0F
    while (posY < size.height) {
        drawLine(
            color,
            Offset(0F, posY),
            Offset(size.width, posY),
            strokeWidth, cap, alpha = alpha
        )
        posY += 100
    }
}


fun DrawScope.drawText(text: String, x: Float, y: Float, textSize: Float, textColor: Color) {
    drawIntoCanvas { canvas ->
        canvas.nativeCanvas.drawText(
            text,
            x,
            y,
            Paint().apply {
                this.textSize = textSize
                color = textColor.toArgb()
                textAlign = Paint.Align.CENTER
            }
        )
    }
}

