package davidmedina.game.app.features.antigravity.mosaic

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.LinkedList
import java.util.Queue
import kotlin.random.Random

class ColorMosaicViewModel : ViewModel() {

    private val _bitmapState = MutableStateFlow<Bitmap?>(null)
    val bitmapState = _bitmapState.asStateFlow()

    private var width = 0
    private var height = 0

    fun init(w: Int, h: Int) {
        if (width == w && height == h && _bitmapState.value != null) return
        width = w
        height = h
        generateShapes()
    }

    fun generateShapes() {
        if (width <= 0 || height <= 0) return
        
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 5f
            isAntiAlias = true
        }

        // Draw random shapes
        for (i in 0..15) {
            when (Random.nextInt(3)) {
                0 -> { // Circle
                    val cx = Random.nextFloat() * width
                    val cy = Random.nextFloat() * height
                    val radius = Random.nextFloat() * 200f + 50f
                    canvas.drawCircle(cx, cy, radius, paint)
                }
                1 -> { // Rectangle
                    val left = Random.nextFloat() * width
                    val top = Random.nextFloat() * height
                    val right = left + Random.nextFloat() * 300f + 50f
                    val bottom = top + Random.nextFloat() * 300f + 50f
                    canvas.drawRect(left, top, right, bottom, paint)
                }
                2 -> { // Random Path
                    val path = Path()
                    path.moveTo(Random.nextFloat() * width, Random.nextFloat() * height)
                    path.lineTo(Random.nextFloat() * width, Random.nextFloat() * height)
                    path.lineTo(Random.nextFloat() * width, Random.nextFloat() * height)
                    path.close()
                    canvas.drawPath(path, paint)
                }
            }
        }
        _bitmapState.value = bitmap
    }

    fun fillZone(x: Int, y: Int) {
        val currentBitmap = _bitmapState.value ?: return
        if (x < 0 || x >= width || y < 0 || y >= height) return

        viewModelScope.launch(Dispatchers.Default) {
            // Create a mutable copy to modify
            val newBitmap = currentBitmap.copy(Bitmap.Config.ARGB_8888, true)
            
            val targetColor = newBitmap.getPixel(x, y)
            val replacementColor = getRandomColor()

            if (targetColor != replacementColor && targetColor != Color.BLACK) {
                floodFill(newBitmap, x, y, targetColor, replacementColor)
                _bitmapState.value = newBitmap
            }
        }
    }

    private fun floodFill(bitmap: Bitmap, x: Int, y: Int, targetColor: Int, replacementColor: Int) {
        val width = bitmap.width
        val height = bitmap.height
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        
        queue.add(Pair(x, y))
        
        // Use an array to keep track of visited pixels to avoid infinite loops and redundant checks
        // For a simple bitmap, checking pixel color is okay, but visited array is safer/faster for large areas
        // However, direct pixel check is memory efficient. 
        // We must ensure targetColor != replacementColor (checked in caller)
        
        while (queue.isNotEmpty()) {
            val (cx, cy) = queue.remove()
            
            if (cx < 0 || cx >= width || cy < 0 || cy >= height) continue
            
            val pixelColor = bitmap.getPixel(cx, cy)
            if (pixelColor != targetColor) continue
            
            bitmap.setPixel(cx, cy, replacementColor)
            
            queue.add(Pair(cx + 1, cy))
            queue.add(Pair(cx - 1, cy))
            queue.add(Pair(cx, cy + 1))
            queue.add(Pair(cx, cy - 1))
        }
    }

    private fun getRandomColor(): Int {
        return Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    }
}
