package davidmedina.game.app.features.antigravity.handtracking

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class Particle(
    val id: Long = Random.nextLong(),
    val position: Offset,
    val velocity: Offset,
    val radius: Float,
    val color: Color
) {
    val mass: Float get() = radius * radius
}

class HandGravityViewModel : ViewModel() {

    private val _particles = MutableStateFlow<List<Particle>>(emptyList())
    val particles = _particles.asStateFlow()

    private val _handState = MutableStateFlow(HandState())
    val handState = _handState.asStateFlow()

    private var screenWidth = 0f
    private var screenHeight = 0f
    
    private val gravityConstant = 5f  // Increased from 3f for stronger forces
    private val maxParticles = 50
    private val particleColors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFFA855F7), // Purple
        Color(0xFFEC4899), // Pink
        Color(0xFFF59E0B), // Amber
        Color(0xFF10B981), // Emerald
        Color(0xFF3B82F6), // Blue,
    )

    init {
        startPhysicsLoop()
    }

    fun updateScreenSize(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
        if (_particles.value.isEmpty()) {
            spawnInitialParticles()
        }
    }

    fun updateHandState(state: HandState) {
        _handState.value = state
    }

    private fun spawnInitialParticles() {
        val particles = mutableListOf<Particle>()
        
        repeat(30) {
            particles.add(createRandomParticle())
        }
        
        _particles.value = particles
    }

    private fun createRandomParticle(): Particle {
        // Spawn from edges
        val spawnFromTop = Random.nextBoolean()
        val position = if (spawnFromTop) {
            if (Random.nextBoolean()) {
                // Top or bottom
                Offset(
                    Random.nextFloat() * screenWidth,
                    if (Random.nextBoolean()) 0f else screenHeight
                )
            } else {
                // Left or right
                Offset(
                    if (Random.nextBoolean()) 0f else screenWidth,
                    Random.nextFloat() * screenHeight
                )
            }
        } else {
            Offset(
                Random.nextFloat() * screenWidth,
                Random.nextFloat() * screenHeight
            )
        }

        return Particle(
            position = position,
            velocity = Offset(
                (Random.nextFloat() - 0.5f) * 3f,
                (Random.nextFloat() - 0.5f) * 3f
            ),
            radius = Random.nextFloat() * 15f + 5f,
            color = particleColors.random()
        )
    }

    private fun startPhysicsLoop() {
        viewModelScope.launch {
            while (isActive) {
                updatePhysics()
                delay(16) // ~60 FPS
            }
        }
    }

    private fun updatePhysics() {
        val currentParticles = _particles.value
        if (currentParticles.isEmpty()) return

        val handState = _handState.value
        val nextParticles = currentParticles.map { it.copy() }.toMutableList()

        // Apply hand gravity if hand is detected
        if (handState.isDetected) {
            val forceMultiplier = when (handState.gesture) {
                GestureType.PINCH -> 15f // MUCH stronger attraction (black hole)
                GestureType.OPEN_PALM -> -5f // Strong repulsion (white hole)
                GestureType.IDLE -> 0f
            }

            if (forceMultiplier != 0f) {
                for (i in nextParticles.indices) {
                    val particle = nextParticles[i]
                    val delta = handState.position - particle.position
                    val distance = delta.getDistance()

                    if (distance > 1f) {
                        val forceMagnitude = (gravityConstant * particle.mass * 100f) / 
                            max(distance * distance, 100f)
                        val forceDirection = delta / distance
                        val force = forceDirection * forceMagnitude * forceMultiplier

                        nextParticles[i] = particle.copy(
                            velocity = particle.velocity + force / particle.mass
                        )
                    }
                }
            }
        }

        // Update positions and handle boundaries
        val survivingParticles = mutableListOf<Particle>()
        
        for (particle in nextParticles) {
            var newPos = particle.position + particle.velocity
            var newVel = particle.velocity

            // Wall collisions with bounce
            if (newPos.x - particle.radius < 0 || newPos.x + particle.radius > screenWidth) {
                newVel = newVel.copy(x = -newVel.x * 0.7f)
                newPos = newPos.copy(
                    x = if (newPos.x - particle.radius < 0) 
                        particle.radius 
                    else 
                        screenWidth - particle.radius
                )
            }

            if (newPos.y - particle.radius < 0 || newPos.y + particle.radius > screenHeight) {
                newVel = newVel.copy(y = -newVel.y * 0.7f)
                newPos = newPos.copy(
                    y = if (newPos.y - particle.radius < 0) 
                        particle.radius 
                    else 
                        screenHeight - particle.radius
                )
            }

            // Apply friction
            newVel = newVel * 0.98f

            // Check if particle is within bounds (with some margin)
            val margin = 100f
            val inBounds = newPos.x > -margin && 
                          newPos.x < screenWidth + margin &&
                          newPos.y > -margin && 
                          newPos.y < screenHeight + margin

            if (inBounds) {
                survivingParticles.add(particle.copy(position = newPos, velocity = newVel))
            }
        }

        // Spawn new particles if below threshold
        while (survivingParticles.size < min(maxParticles, 30)) {
            survivingParticles.add(createRandomParticle())
        }

        _particles.value = survivingParticles
    }
}
