package davidmedina.game.app.features.antigravity

import android.content.Context
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.sqrt

enum class GestureType {
    IDLE,
    PINCH,
    OPEN_PALM
}

data class HandState(
    val position: Offset = Offset.Zero,
    val gesture: GestureType = GestureType.IDLE,
    val isDetected: Boolean = false,
    val confidence: Float = 0f
)

class HandTrackingManager(private val context: Context) {

    private val _handState = MutableStateFlow(HandState())
    val handState = _handState.asStateFlow()

    private var camera: Camera? = null
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    
    private var handLandmarker: HandLandmarker? = null
    
    private var screenWidth = 1f
    private var screenHeight = 1f
    
    // Track camera image dimensions for proper coordinate mapping
    private var cameraImageWidth = 1f
    private var cameraImageHeight = 1f
    
    // Smoothing buffer for hand positions
    private val positionBuffer = mutableListOf<Offset>()
    private val bufferSize = 3

    init {
        setupHandLandmarker()
    }

    private fun setupHandLandmarker() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("hand_landmarker.task")
                .build()

            val options = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setNumHands(1)
                .setMinHandDetectionConfidence(0.5f)
                .setMinHandPresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener { result, image ->
                    handleHandResult(result, image)
                }
                .setErrorListener { error ->
                    Timber.e(error, "Hand landmarker error")
                }
                .build()

            handLandmarker = HandLandmarker.createFromOptions(context, options)
        } catch (e: Exception) {
            Timber.e(e, "Failed to setup hand landmarker")
        }
    }

    fun updateScreenSize(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
        Timber.d("Screen size updated: ${width}x${height}")
    }

    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Timber.e(exc, "Camera binding failed")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        try {
            val bitmap = imageProxy.toBitmap()
            val mpImage = BitmapImageBuilder(bitmap).build()
            val frameTime = System.currentTimeMillis()
            
            handLandmarker?.detectAsync(mpImage, frameTime)
        } catch (e: Exception) {
            Timber.e(e, "Error processing image")
        } finally {
            imageProxy.close()
        }
    }

    private fun handleHandResult(result: HandLandmarkerResult, image: MPImage) {
        if (result.landmarks().isNotEmpty()) {
            val landmarks = result.landmarks()[0]
            processHand(landmarks, image.width, image.height)
        } else {
            _handState.value = HandState(isDetected = false)
            positionBuffer.clear()
        }
    }

    private fun processHand(landmarks: List<com.google.mediapipe.tasks.components.containers.NormalizedLandmark>, imageWidth: Int, imageHeight: Int) {
        // Update camera image dimensions for debugging
        cameraImageWidth = imageWidth.toFloat()
        cameraImageHeight = imageHeight.toFloat()
        
        // Get index finger tip (landmark 8) and thumb tip (landmark 4)
        val indexTip = landmarks.getOrNull(8)
        val thumbTip = landmarks.getOrNull(4)
        
        // Calculate palm center as average of key landmarks
        // 0=wrist, 5=index_base, 9=middle_base, 13=ring_base, 17=pinky_base
        val wrist = landmarks.getOrNull(0)
        val indexBase = landmarks.getOrNull(5)
        val middleBase = landmarks.getOrNull(9)
        val ringBase = landmarks.getOrNull(13)
        val pinkyBase = landmarks.getOrNull(17)

        if (indexTip != null && thumbTip != null && wrist != null && 
            indexBase != null && middleBase != null && ringBase != null && pinkyBase != null) {
            
            // Calculate palm center (average of wrist + 4 finger bases)
            val centerX = (wrist.x() + indexBase.x() + middleBase.x() + ringBase.x() + pinkyBase.x()) / 5f
            val centerY = (wrist.y() + indexBase.y() + middleBase.y() + ringBase.y() + pinkyBase.y()) / 5f

            // Apply camera sensor rotation (front camera is typically 270° on Android)
            // Rotate coordinates from sensor space to screen space
            // 270° rotation: (x, y) -> (y, 1-x)
            val rotatedX = centerY
            val rotatedY = 1f - centerX

            // Map to screen coordinates (already mirrored by rotation)
            val screenPos = Offset(
                rotatedX * screenWidth,
                rotatedY * screenHeight
            )

            // Smooth position with buffer
            positionBuffer.add(screenPos)
            if (positionBuffer.size > bufferSize) {
                positionBuffer.removeAt(0)
            }
            
            val smoothedPos = if (positionBuffer.isNotEmpty()) {
                Offset(
                    positionBuffer.map { it.x }.average().toFloat(),
                    positionBuffer.map { it.y }.average().toFloat()
                )
            } else {
                screenPos
            }

            // Calculate pinch distance (in normalized coordinates)
            val dx = indexTip.x() - thumbTip.x()
            val dy = indexTip.y() - thumbTip.y()
            val pinchDistance = sqrt(dx * dx + dy * dy) * 1000f // Scale to reasonable range

            // Detect gesture with hysteresis
            val currentGesture = _handState.value.gesture
            val newGesture = when {
                pinchDistance < 40f -> GestureType.PINCH
                pinchDistance > 100f -> GestureType.OPEN_PALM
                currentGesture == GestureType.PINCH && pinchDistance < 80f -> GestureType.PINCH
                currentGesture == GestureType.OPEN_PALM && pinchDistance > 60f -> GestureType.OPEN_PALM
                else -> GestureType.IDLE
            }

            Timber.d("Hand pos: palm_center=(%.2f, %.2f) rotated=(%.2f, %.2f) screen=(%.0f, %.0f)", 
                centerX, centerY, rotatedX, rotatedY, smoothedPos.x, smoothedPos.y)

            _handState.value = HandState(
                position = smoothedPos,
                gesture = newGesture,
                isDetected = true,
                confidence = 1f
            )
        }
    }

    fun shutdown() {
        handLandmarker?.close()
        handLandmarker = null
        cameraExecutor.shutdown()
    }
}
