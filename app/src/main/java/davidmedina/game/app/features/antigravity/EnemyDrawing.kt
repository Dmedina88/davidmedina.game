package davidmedina.game.app.features.antigravity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Procedurally draw enemies using Canvas based on their type
 */
@Composable
fun EnemyCanvas(enemy: DungeonEnemy, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(200.dp).padding(16.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        when (enemy.name) {
            "Rat" -> drawRat(centerX, centerY)
            "Spider" -> drawSpider(centerX, centerY)
            "Goblin" -> drawGoblin(centerX, centerY)
            "Wolf" -> drawWolf(centerX, centerY)
            "Skeleton" -> drawSkeleton(centerX, centerY)
            "Zombie" -> drawZombie(centerX, centerY)
            "Orc" -> drawOrc(centerX, centerY)
            "Troll" -> drawTroll(centerX, centerY)
            "Vampire" -> drawVampire(centerX, centerY)
            "Dragon" -> drawDragon(centerX, centerY)
            else -> drawGenericEnemy(centerX, centerY)
        }
    }
}

private fun DrawScope.drawRat(centerX: Float, centerY: Float) {
    val baseSize = size.minDimension * 0.3f
    
    // Body
    drawCircle(
        color = Color(0xFF8B4513),
        radius = baseSize * 0.6f,
        center = Offset(centerX, centerY)
    )
    
    // Head
    drawCircle(
        color = Color(0xFFA0522D),
        radius = baseSize * 0.4f,
        center = Offset(centerX + baseSize * 0.5f, centerY - baseSize * 0.3f)
    )
    
    // Ears
    drawCircle(
        color = Color(0xFF8B4513),
        radius = baseSize * 0.2f,
        center = Offset(centerX + baseSize * 0.6f, centerY - baseSize * 0.7f)
    )
    drawCircle(
        color = Color(0xFF8B4513),
        radius = baseSize * 0.2f,
        center = Offset(centerX + baseSize * 0.9f, centerY - baseSize * 0.6f)
    )
    
    // Eyes
    drawCircle(
        color = Color.Red,
        radius = baseSize * 0.08f,
        center = Offset(centerX + baseSize * 0.6f, centerY - baseSize * 0.35f)
    )
    
    // Tail
    val tailPath = Path().apply {
        moveTo(centerX - baseSize * 0.6f, centerY)
        cubicTo(
            centerX - baseSize, centerY - baseSize * 0.5f,
            centerX - baseSize * 1.2f, centerY + baseSize * 0.3f,
            centerX - baseSize * 1.5f, centerY
        )
    }
    drawPath(tailPath, color = Color(0xFF8B4513), style = Stroke(width = 4f))
}

private fun DrawScope.drawGoblin(centerX: Float, centerY: Float) {
    val baseSize = size.minDimension * 0.35f
    
    // Body (lumpy shape)
    drawOval(
        color = Color(0xFF00AA00),
        topLeft = Offset(centerX - baseSize * 0.5f, centerY - baseSize * 0.3f),
        size = Size(baseSize, baseSize * 1.2f)
    )
    
    // Head
    drawCircle(
        color = Color(0xFF00CC00),
        radius = baseSize * 0.45f,
        center = Offset(centerX, centerY - baseSize * 0.8f)
    )
    
    // Eyes (menacing)
    drawCircle(
        color = Color.Yellow,
        radius = baseSize * 0.12f,
        center = Offset(centerX - baseSize * 0.2f, centerY - baseSize * 0.9f)
    )
    drawCircle(
        color = Color.Yellow,
        radius = baseSize * 0.12f,
        center = Offset(centerX + baseSize * 0.2f, centerY - baseSize * 0.9f)
    )
    
    // Pupils
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.06f,
        center = Offset(centerX - baseSize * 0.2f, centerY - baseSize * 0.9f)
    )
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.06f,
        center = Offset(centerX + baseSize * 0.2f, centerY - baseSize * 0.9f)
    )
    
    // Pointy ears
    val earPath = Path().apply {
        moveTo(centerX - baseSize * 0.45f, centerY - baseSize * 0.7f)
        lineTo(centerX - baseSize * 0.7f, centerY - baseSize)
        lineTo(centerX - baseSize * 0.35f, centerY - baseSize * 0.85f)
        close()
    }
    drawPath(earPath, color = Color(0xFF00AA00))
    
    val earPath2 = Path().apply {
        moveTo(centerX + baseSize * 0.45f, centerY - baseSize * 0.7f)
        lineTo(centerX + baseSize * 0.7f, centerY - baseSize)
        lineTo(centerX + baseSize * 0.35f, centerY - baseSize * 0.85f)
        close()
    }
    drawPath(earPath2, color = Color(0xFF00AA00))
}

private fun DrawScope.drawSkeleton(centerX: Float, centerY: Float) {
    val baseSize = size.minDimension * 0.35f
    
    // Skull
    drawCircle(
        color = Color.White,
        radius = baseSize * 0.5f,
        center = Offset(centerX, centerY - baseSize * 0.6f),
        style = Stroke(width = 3f)
    )
    
    drawOval(
        color = Color.White,
        topLeft = Offset(centerX - baseSize * 0.35f, centerY - baseSize * 0.9f),
        size = Size(baseSize * 0.7f, baseSize * 0.6f),
        style = Stroke(width = 3f)
    )
    
    // Eye sockets
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.15f,
        center = Offset(centerX - baseSize * 0.2f, centerY - baseSize * 0.7f)
    )
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.15f,
        center = Offset(centerX + baseSize * 0.2f, centerY - baseSize * 0.7f)
    )
    
    // Spine/ribs
    for (i in 0..3) {
        val y = centerY - baseSize * 0.2f + i * baseSize * 0.25f
        drawLine(
            color = Color.White,
            start = Offset(centerX - baseSize * 0.3f, y),
            end = Offset(centerX + baseSize * 0.3f, y),
            strokeWidth = 3f
        )
    }
    
    // Vertical spine
    drawLine(
        color = Color.White,
        start = Offset(centerX, centerY - baseSize * 0.3f),
        end = Offset(centerX, centerY + baseSize * 0.5f),
        strokeWidth = 4f
    )
}

private fun DrawScope.drawOrc(centerX: Float, centerY: Float) {
    val baseSize = size.minDimension * 0.4f
    
    // Muscular body
    drawOval(
        color = Color(0xFF4A4A4A),
        topLeft = Offset(centerX - baseSize * 0.6f, centerY - baseSize * 0.2f),
        size = Size(baseSize * 1.2f, baseSize * 1.4f)
    )
    
    // Head (brutish)
    drawOval(
        color = Color(0xFF556B2F),
        topLeft = Offset(centerX - baseSize * 0.5f, centerY - baseSize),
        size = Size(baseSize, baseSize * 0.8f)
    )
    
    // Tusks
    val tuskPath1 = Path().apply {
        moveTo(centerX - baseSize * 0.2f, centerY - baseSize * 0.4f)
        lineTo(centerX - baseSize * 0.35f, centerY - baseSize * 0.2f)
        lineTo(centerX - baseSize * 0.15f, centerY - baseSize * 0.3f)
        close()
    }
    drawPath(tuskPath1, color = Color.White)
    
    val tuskPath2 = Path().apply {
        moveTo(centerX + baseSize * 0.2f, centerY - baseSize * 0.4f)
        lineTo(centerX + baseSize * 0.35f, centerY - baseSize * 0.2f)
        lineTo(centerX + baseSize * 0.15f, centerY - baseSize * 0.3f)
        close()
    }
    drawPath(tuskPath2, color = Color.White)
    
    // Angry eyes
    drawCircle(
        color = Color.Red,
        radius = baseSize * 0.1f,
        center = Offset(centerX - baseSize * 0.2f, centerY - baseSize * 0.7f)
    )
    drawCircle(
        color = Color.Red,
        radius = baseSize * 0.1f,
        center = Offset(centerX + baseSize * 0.2f, centerY - baseSize * 0.7f)
    )
}

private fun DrawScope.drawDragon(centerX: Float, centerY: Float) {
    val baseSize = size.minDimension * 0.4f
    
    // Dragon body (serpentine)
    drawOval(
        color = Color.Magenta,
        topLeft = Offset(centerX - baseSize * 0.7f, centerY - baseSize * 0.3f),
        size = Size(baseSize * 1.4f, baseSize)
    )
    
    // Head/snout
    drawOval(
        color = Color(0xFFFF00FF),
        topLeft = Offset(centerX + baseSize * 0.3f, centerY - baseSize * 0.6f),
        size = Size(baseSize * 0.8f, baseSize * 0.6f)
    )
    
    // Wings
    val wingPath1 = Path().apply {
        moveTo(centerX - baseSize * 0.3f, centerY)
        lineTo(centerX - baseSize * 1.2f, centerY - baseSize * 0.8f)
        lineTo(centerX - baseSize * 0.5f, centerY - baseSize * 0.2f)
        close()
    }
    drawPath(wingPath1, color = Color(0xFFAA00AA), alpha = 0.7f)
    
    val wingPath2 = Path().apply {
        moveTo(centerX - baseSize * 0.3f, centerY)
        lineTo(centerX - baseSize * 1.2f, centerY + baseSize * 0.8f)
        lineTo(centerX - baseSize * 0.5f, centerY + baseSize * 0.2f)
        close()
    }
    drawPath(wingPath2, color = Color(0xFFAA00AA), alpha = 0.7f)
    
    // Eyes (fierce)
    drawCircle(
        color = Color.Yellow,
        radius = baseSize * 0.12f,
        center = Offset(centerX + baseSize * 0.7f, centerY - baseSize * 0.4f)
    )
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.06f,
        center = Offset(centerX + baseSize * 0.7f, centerY - baseSize * 0.4f)
    )
    
    // Horns
    drawLine(
        color = Color(0xFF880088),
        start = Offset(centerX + baseSize * 0.5f, centerY - baseSize * 0.6f),
        end = Offset(centerX + baseSize * 0.4f, centerY - baseSize),
        strokeWidth = 6f
    )
    drawLine(
        color = Color(0xFF880088),
        start = Offset(centerX + baseSize * 0.9f, centerY - baseSize * 0.5f),
        end = Offset(centerX + baseSize, centerY - baseSize * 0.9f),
        strokeWidth = 6f
    )
}

private fun DrawScope.drawSpider(centerX: Float, centerY: Float) {
    val baseSize = size.minDimension * 0.35f
    
    // Body (oval)
    drawOval(
        color = Color.Black,
        topLeft = Offset(centerX - baseSize * 0.4f, centerY - baseSize * 0.2f),
        size = Size(baseSize * 0.8f, baseSize * 0.6f)
    )
    
    // Head
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.25f,
        center = Offset(centerX, centerY - baseSize * 0.5f)
    )
    
    // 8 legs (4 on each side)
    for (i in 0..3) {
        val angle = -PI / 2 + (i * PI / 6)
        val legLength = baseSize * 0.8f
        
        // Left legs
        val leftPath = Path().apply {
            moveTo(centerX - baseSize * 0.3f, centerY)
            lineTo(
                centerX - baseSize * 0.3f - legLength * cos(angle).toFloat(),
                centerY + legLength * sin(angle).toFloat()
            )
        }
        drawPath(leftPath, color = Color.Black, style = Stroke(width = 3f))
        
        // Right legs
        val rightPath = Path().apply {
            moveTo(centerX + baseSize * 0.3f, centerY)
            lineTo(
                centerX + baseSize * 0.3f + legLength * cos(angle).toFloat(),
                centerY + legLength * sin(angle).toFloat()
            )
        }
        drawPath(rightPath, color = Color.Black, style = Stroke(width = 3f))
    }
    
    // Multiple eyes (creepy!)
    val eyePositions = listOf(
        Offset(centerX - baseSize * 0.1f, centerY - baseSize * 0.55f),
        Offset(centerX + baseSize * 0.1f, centerY - baseSize * 0.55f),
        Offset(centerX - baseSize * 0.15f, centerY - baseSize * 0.45f),
        Offset(centerX + baseSize * 0.15f, centerY - baseSize * 0.45f)
    )
    
    eyePositions.forEach { pos ->
        drawCircle(color = Color.Red, radius = baseSize * 0.05f, center = pos)
    }
}

private fun DrawScope.drawWolf(centerX: Float, centerY: Float) {
    val baseSize = size.minDimension * 0.35f
    
    // Body (gray fur)
    drawOval(
        color = Color(0xFF6B6B6B),
        topLeft = Offset(centerX - baseSize * 0.6f, centerY - baseSize * 0.2f),
        size = Size(baseSize * 1.2f, baseSize)
    )
    
    // Head/snout
    drawOval(
        color = Color(0xFF808080),
        topLeft = Offset(centerX + baseSize * 0.2f, centerY - baseSize * 0.5f),
        size = Size(baseSize * 0.6f, baseSize * 0.5f)
    )
    
    // Ears (pointy)
    val earPath1 = Path().apply {
        moveTo(centerX + baseSize * 0.3f, centerY - baseSize * 0.5f)
        lineTo(centerX + baseSize * 0.2f, centerY - baseSize * 0.9f)
        lineTo(centerX + baseSize * 0.45f, centerY - baseSize * 0.6f)
        close()
    }
    drawPath(earPath1, color = Color(0xFF555555))
    
    val earPath2 = Path().apply {
        moveTo(centerX + baseSize * 0.6f, centerY - baseSize * 0.5f)
        lineTo(centerX + baseSize * 0.65f, centerY - baseSize * 0.9f)
        lineTo(centerX + baseSize * 0.75f, centerY - baseSize * 0.6f)
        close()
    }
    drawPath(earPath2, color = Color(0xFF555555))
    
    // Eyes (yellow, fierce)
    drawCircle(
        color = Color.Yellow,
        radius = baseSize * 0.1f,
        center = Offset(centerX + baseSize * 0.4f, centerY - baseSize * 0.35f)
    )
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.05f,
        center = Offset(centerX + baseSize * 0.4f, centerY - baseSize * 0.35f)
    )
    
    // Nose
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.08f,
        center = Offset(centerX + baseSize * 0.75f, centerY - baseSize * 0.25f)
    )
    
    // Fangs
    drawLine(
        color = Color.White,
        start = Offset(centerX + baseSize * 0.7f, centerY - baseSize * 0.15f),
        end = Offset(centerX + baseSize * 0.65f, centerY),
        strokeWidth = 4f
    )
}

private fun DrawScope.drawZombie(centerX: Float, centerY: Float) {
    val baseSize = size.minDimension * 0.35f
    
    // Decaying body (greenish)
    drawOval(
        color = Color(0xFF6B8E23),
        topLeft = Offset(centerX - baseSize * 0.5f, centerY - baseSize * 0.2f),
        size = Size(baseSize, baseSize * 1.3f)
    )
    
    // Head (rotting)
    drawCircle(
        color = Color(0xFF789922),
        radius = baseSize * 0.45f,
        center = Offset(centerX, centerY - baseSize * 0.7f)
    )
    
    // Skull showing through (patches)
    drawCircle(
        color = Color(0xFFBBBBBB),
        radius = baseSize * 0.15f,
        center = Offset(centerX - baseSize * 0.2f, centerY - baseSize * 0.6f)
    )
    
    // Eyes (dead, white)
    drawCircle(
        color = Color.White,
        radius = baseSize * 0.12f,
        center = Offset(centerX - baseSize * 0.2f, centerY - baseSize * 0.8f)
    )
    drawCircle(
        color = Color.White,
        radius = baseSize * 0.12f,
        center = Offset(centerX + baseSize * 0.2f, centerY - baseSize * 0.8f)
    )
    
    // Pupils (off-center, lifeless)
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.05f,
        center = Offset(centerX - baseSize * 0.15f, centerY - baseSize * 0.75f)
    )
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.05f,
        center = Offset(centerX + baseSize * 0.25f, centerY - baseSize * 0.85f)
    )
    
    // Mouth (open, groaning)
    drawOval(
        color = Color.Black,
        topLeft = Offset(centerX - baseSize * 0.15f, centerY - baseSize * 0.5f),
        size = Size(baseSize * 0.3f, baseSize * 0.2f)
    )
    
    // Arms reaching out
    drawLine(
        color = Color(0xFF6B8E23),
        start = Offset(centerX - baseSize * 0.5f, centerY),
        end = Offset(centerX - baseSize * 1.2f, centerY - baseSize * 0.3f),
        strokeWidth = 8f
    )
    drawLine(
        color = Color(0xFF6B8E23),
        start = Offset(centerX + baseSize * 0.5f, centerY),
        end = Offset(centerX + baseSize * 1.2f, centerY - baseSize * 0.2f),
        strokeWidth = 8f
    )
}

private fun DrawScope.drawTroll(centerX: Float, centerY: Float) {
    val baseSize = size.minDimension * 0.4f
    
    // Large, brutish body (gray-green)
    drawOval(
        color = Color(0xFF556B2F),
        topLeft = Offset(centerX - baseSize * 0.7f, centerY - baseSize * 0.3f),
        size = Size(baseSize * 1.4f, baseSize * 1.6f)
    )
    
    // Massive head
    drawOval(
        color = Color(0xFF6B7B3F),
        topLeft = Offset(centerX - baseSize * 0.6f, centerY - baseSize * 1.1f),
        size = Size(baseSize * 1.2f, baseSize * 0.9f)
    )
    
    // Big nose
    drawCircle(
        color = Color(0xFF4A5A2F),
        radius = baseSize * 0.2f,
        center = Offset(centerX, centerY - baseSize * 0.7f)
    )
    
    // Small, mean eyes
    drawCircle(
        color = Color.Yellow,
        radius = baseSize * 0.08f,
        center = Offset(centerX - baseSize * 0.3f, centerY - baseSize * 0.9f)
    )
    drawCircle(
        color = Color.Yellow,
        radius = baseSize * 0.08f,
        center = Offset(centerX + baseSize * 0.3f, centerY - baseSize * 0.9f)
    )
    
    // Pupils
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.04f,
        center = Offset(centerX - baseSize * 0.3f, centerY - baseSize * 0.9f)
    )
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.04f,
        center = Offset(centerX + baseSize * 0.3f, centerY - baseSize * 0.9f)
    )
    
    // Tusks/teeth
    drawPath(
        Path().apply {
            moveTo(centerX - baseSize * 0.1f, centerY - baseSize * 0.5f)
            lineTo(centerX - baseSize * 0.25f, centerY - baseSize * 0.3f)
            lineTo(centerX - baseSize * 0.05f, centerY - baseSize * 0.4f)
            close()
        },
        color = Color.White
    )
    drawPath(
        Path().apply {
            moveTo(centerX + baseSize * 0.1f, centerY - baseSize * 0.5f)
            lineTo(centerX + baseSize * 0.25f, centerY - baseSize * 0.3f)
            lineTo(centerX + baseSize * 0.05f, centerY - baseSize * 0.4f)
            close()
        },
        color = Color.White
    )
    
    // Club/weapon (optional detail)
    drawLine(
        color = Color(0xFF8B4513),
        start = Offset(centerX + baseSize * 0.8f, centerY + baseSize * 0.5f),
        end = Offset(centerX + baseSize * 1.3f, centerY - baseSize * 0.3f),
        strokeWidth = 10f
    )
}

private fun DrawScope.drawVampire(centerX: Float, centerY: Float) {
    val baseSize = size.minDimension * 0.35f
    
    // Cape (dramatic)
    val capePath = Path().apply {
        moveTo(centerX, centerY - baseSize * 0.6f)
        lineTo(centerX - baseSize * 1.1f, centerY - baseSize * 0.3f)
        lineTo(centerX - baseSize * 1.2f, centerY + baseSize * 0.8f)
        lineTo(centerX - baseSize * 0.5f, centerY + baseSize * 0.6f)
        close()
    }
    drawPath(capePath, color = Color(0xFF8B0000), alpha = 0.8f)
    
    val capePath2 = Path().apply {
        moveTo(centerX, centerY - baseSize * 0.6f)
        lineTo(centerX + baseSize * 1.1f, centerY - baseSize * 0.3f)
        lineTo(centerX + baseSize * 1.2f, centerY + baseSize * 0.8f)
        lineTo(centerX + baseSize * 0.5f, centerY + baseSize * 0.6f)
        close()
    }
    drawPath(capePath2, color = Color(0xFF8B0000), alpha = 0.8f)
    
    // Body (pale)
    drawOval(
        color = Color(0xFFEEEEEE),
        topLeft = Offset(centerX - baseSize * 0.4f, centerY - baseSize * 0.2f),
        size = Size(baseSize * 0.8f, baseSize * 1.2f)
    )
    
    // Head (very pale)
    drawCircle(
        color = Color(0xFFFAFAFA),
        radius = baseSize * 0.4f,
        center = Offset(centerX, centerY - baseSize * 0.7f)
    )
    
    // Eyes (red, menacing)
    drawCircle(
        color = Color.Red,
        radius = baseSize * 0.12f,
        center = Offset(centerX - baseSize * 0.15f, centerY - baseSize * 0.75f)
    )
    drawCircle(
        color = Color.Red,
        radius = baseSize * 0.12f,
        center = Offset(centerX + baseSize * 0.15f, centerY - baseSize * 0.75f)
    )
    
    // Pupils (intense)
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.06f,
        center = Offset(centerX - baseSize * 0.15f, centerY - baseSize * 0.75f)
    )
    drawCircle(
        color = Color.Black,
        radius = baseSize * 0.06f,
        center = Offset(centerX + baseSize * 0.15f, centerY - baseSize * 0.75f)
    )
    
    // Fangs
    drawLine(
        color = Color.White,
        start = Offset(centerX - baseSize * 0.1f, centerY - baseSize * 0.5f),
        end = Offset(centerX - baseSize * 0.08f, centerY - baseSize * 0.35f),
        strokeWidth = 5f
    )
    drawLine(
        color = Color.White,
        start = Offset(centerX + baseSize * 0.1f, centerY - baseSize * 0.5f),
        end = Offset(centerX + baseSize * 0.08f, centerY - baseSize * 0.35f),
        strokeWidth = 5f
    )
    
    // Slicked back hair
    drawOval(
        color = Color.Black,
        topLeft = Offset(centerX - baseSize * 0.35f, centerY - baseSize * 1.1f),
        size = Size(baseSize * 0.7f, baseSize * 0.4f)
    )
}

private fun DrawScope.drawGenericEnemy(centerX: Float, centerY: Float) {
    val baseSize = size.minDimension * 0.35f
    
    // Simple monster silhouette
    drawCircle(
        color = Color.Red,
        radius = baseSize * 0.5f,
        center = Offset(centerX, centerY)
    )
    
    drawCircle(
        color = Color.Yellow,
        radius = baseSize * 0.1f,
        center = Offset(centerX - baseSize * 0.2f, centerY - baseSize * 0.1f)
    )
    drawCircle(
        color = Color.Yellow,
        radius = baseSize * 0.1f,
        center = Offset(centerX + baseSize * 0.2f, centerY - baseSize * 0.1f)
    )
}
