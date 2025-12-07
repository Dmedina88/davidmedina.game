package davidmedina.game.app.features.antigravity.gravity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GravityObject(
    val id: Long = Random.nextLong(),
    val position: Offset,
    val velocity: Offset,
    val radius: Float,
    val color: Color
) {
    val mass: Float get() = radius * radius // Mass proportional to area
}

class GravityPlaygroundViewModel : ViewModel() {

    private val _gravityObjects = MutableStateFlow<List<GravityObject>>(emptyList())
    val gravityObjects = _gravityObjects.asStateFlow()

    private val _isGravityReversed = MutableStateFlow(false)
    val isGravityReversed = _isGravityReversed.asStateFlow()

    private var screenWidth = 0f
    private var screenHeight = 0f
    private val G = 0.5f // Gravitational Constant

    init {
        startPhysicsLoop()
    }

    fun updateScreenSize(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
        if (_gravityObjects.value.isEmpty()) {
            spawnInitialObjects()
        }
    }

    fun toggleGravity() {
        _isGravityReversed.value = !_isGravityReversed.value
    }

    private fun spawnInitialObjects() {
        val objects = mutableListOf<GravityObject>()
        val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Cyan, Color.Magenta)
        
        for (i in 0..15) {
            objects.add(
                GravityObject(
                    position = Offset(
                        Random.nextFloat() * screenWidth,
                        Random.nextFloat() * screenHeight
                    ),
                    velocity = Offset(
                        (Random.nextFloat() - 0.5f) * 5f,
                        (Random.nextFloat() - 0.5f) * 5f
                    ),
                    radius = Random.nextFloat() * 30f + 10f,
                    color = colors.random()
                )
            )
        }
        _gravityObjects.value = objects
    }

    private fun startPhysicsLoop() {
        viewModelScope.launch {
            while (isActive) {
                updatePhysics()
                delay(16) // Approx 60 FPS
            }
        }
    }

    private fun updatePhysics() {
        val currentObjects = _gravityObjects.value
        val nextObjects = currentObjects.map { it.copy() }.toMutableList()
        val reverseMultiplier = if (_isGravityReversed.value) -1f else 1f
        
        // Calculate gravitational forces
        for (i in nextObjects.indices) {
            val obj1 = nextObjects[i]
            for (j in i + 1 until nextObjects.size) {
                val obj2 = nextObjects[j]
                
                val delta = obj2.position - obj1.position
                val distance = delta.getDistance()
                
                // Avoid division by zero and extreme forces when too close
                if (distance > obj1.radius + obj2.radius) {
                    val forceMagnitude = (G * obj1.mass * obj2.mass) / (distance * distance)
                    val forceDirection = delta / distance
                    
                    val force = forceDirection * forceMagnitude * reverseMultiplier
                    
                    // F = ma -> a = F/m
                    // We update velocities on the mutable copies
                    // Since we are iterating, we need to be careful. 
                    // Ideally we calculate all forces first then apply, but for simple sim this is okay-ish
                    // But since we are mapping to new objects, we can't easily update 'obj2' while iterating 'obj1' if we use map.
                    // So we use a MutableList of the copies.
                    
                    nextObjects[i] = nextObjects[i].copy(velocity = nextObjects[i].velocity + force / obj1.mass)
                    nextObjects[j] = nextObjects[j].copy(velocity = nextObjects[j].velocity - force / obj2.mass)
                }
            }
        }

        // Apply velocity and collisions
        for (i in nextObjects.indices) {
            var obj = nextObjects[i]
            
            // Update position
            var newPos = obj.position + obj.velocity
            var newVel = obj.velocity

            // Wall collisions
            if (newPos.x - obj.radius < 0 || newPos.x + obj.radius > screenWidth) {
                newVel = newVel.copy(x = -newVel.x * 0.8f)
                val newX = if (newPos.x - obj.radius < 0) obj.radius else screenWidth - obj.radius
                newPos = newPos.copy(x = newX)
            }
            if (newPos.y - obj.radius < 0 || newPos.y + obj.radius > screenHeight) {
                newVel = newVel.copy(y = -newVel.y * 0.8f)
                val newY = if (newPos.y - obj.radius < 0) obj.radius else screenHeight - obj.radius
                newPos = newPos.copy(y = newY)
            }
            
            // Friction
            newVel = newVel * 0.995f
            
            nextObjects[i] = obj.copy(position = newPos, velocity = newVel)
        }
        
        _gravityObjects.value = nextObjects
    }

    fun flingObject(position: Offset, velocity: Offset) {
        val currentList = _gravityObjects.value.toMutableList()
        val index = currentList.indexOfFirst { (it.position - position).getDistance() < it.radius * 2 }
        if (index != -1) {
            val obj = currentList[index]
            currentList[index] = obj.copy(velocity = obj.velocity + velocity * 0.1f)
            _gravityObjects.value = currentList
        }
    }
    
    fun spawnObject(position: Offset) {
         val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Cyan, Color.Magenta)
         val newObj = GravityObject(
            position = position,
            velocity = Offset.Zero,
            radius = Random.nextFloat() * 40f + 20f,
            color = colors.random()
        )
        _gravityObjects.value = _gravityObjects.value + newObj
    }
}
