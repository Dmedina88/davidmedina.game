package davidmedina.game.app.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class TickHandler(
    externalScope: CoroutineScope,
    private val tickIntervalMs: Long = 1000
) {
    private val _tickFlow = MutableSharedFlow<Unit>(replay = 0)
    val tickFlow: SharedFlow<Unit> = _tickFlow

    private var job: Job? = null

    init {
        job = externalScope.launch {
            while (isActive) {
                _tickFlow.emit(Unit)
                delay(tickIntervalMs)
            }
        }
        job?.invokeOnCompletion { cause ->
            if (cause != null && !job!!.isCancelled) {
                throw RuntimeException("TickHandler failed", cause)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
