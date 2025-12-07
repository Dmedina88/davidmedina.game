package davidmedina.game.app.features.antigravity.handtracking

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.koin.androidx.compose.koinViewModel

@Composable
fun HandOfGodScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel = koinViewModel<HandGravityViewModel>()
    
    val handTrackingManager = remember { HandTrackingManager(context) }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Subscribe to hand state updates
    val handState by handTrackingManager.handState.collectAsState()
    LaunchedEffect(handState) {
        viewModel.updateHandState(handState)
    }

    DisposableEffect(Unit) {
        onDispose {
            handTrackingManager.shutdown()
        }
    }

    if (!hasCameraPermission) {
        PermissionDeniedScreen(onRequestPermission = {
            launcher.launch(Manifest.permission.CAMERA)
        })
    } else {
        HandOfGodContent(
            viewModel = viewModel,
            handTrackingManager = handTrackingManager,
            lifecycleOwner = lifecycleOwner,
            handState = handState
        )
    }
}

@Composable
private fun HandOfGodContent(
    viewModel: HandGravityViewModel,
    handTrackingManager: HandTrackingManager,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    handState: HandState
) {
    val particles by viewModel.particles.collectAsState()
    var cameraStarted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onSizeChanged { size ->
                viewModel.updateScreenSize(size.width.toFloat(), size.height.toFloat())
                handTrackingManager.updateScreenSize(size.width.toFloat(), size.height.toFloat())
            }
    ) {
        // Camera Preview Layer
        AndroidView(
            factory = { context ->
                PreviewView(context).apply {
                    if (!cameraStarted) {
                        handTrackingManager.startCamera(lifecycleOwner, this)
                        cameraStarted = true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Particles Layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { particle ->
                drawCircle(
                    color = particle.color,
                    radius = particle.radius,
                    center = particle.position,
                    alpha = 0.8f
                )
            }

            // Draw hand position indicator when detected
            if (handState.isDetected) {
                val indicatorColor = when (handState.gesture) {
                    GestureType.PINCH -> Color(0xFFFF6B6B)
                    GestureType.OPEN_PALM -> Color(0xFF4ECDC4)
                    GestureType.IDLE -> Color.White
                }

                // Draw crosshairs to show exact position
                val crosshairSize = 60f
                drawLine(
                    color = indicatorColor,
                    start = androidx.compose.ui.geometry.Offset(handState.position.x - crosshairSize, handState.position.y),
                    end = androidx.compose.ui.geometry.Offset(handState.position.x + crosshairSize, handState.position.y),
                    strokeWidth = 3f
                )
                drawLine(
                    color = indicatorColor,
                    start = androidx.compose.ui.geometry.Offset(handState.position.x, handState.position.y - crosshairSize),
                    end = androidx.compose.ui.geometry.Offset(handState.position.x, handState.position.y + crosshairSize),
                    strokeWidth = 3f
                )

                // Draw concentric circles
                drawCircle(
                    color = indicatorColor,
                    radius = 50f,
                    center = handState.position,
                    alpha = 0.3f
                )

                drawCircle(
                    color = indicatorColor,
                    radius = 30f,
                    center = handState.position,
                    alpha = 0.5f
                )

                drawCircle(
                    color = indicatorColor,
                    radius = 15f,
                    center = handState.position,
                    alpha = 0.8f
                )
            }
        }

        // Gesture Status Indicator
        GestureIndicator(
            handState = handState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
        )
    }
}

@Composable
private fun GestureIndicator(
    handState: HandState,
    modifier: Modifier = Modifier
) {
    if (handState.isDetected) {
        val (text, emoji, color) = when (handState.gesture) {
            GestureType.PINCH -> Triple("BLACK HOLE", "🕳️", Color(0xFFFF6B6B))
            GestureType.OPEN_PALM -> Triple("WHITE HOLE", "💫", Color(0xFF4ECDC4))
            GestureType.IDLE -> Triple("IDLE", "✋", Color.White)
        }

        Surface(
            modifier = modifier,
            color = color.copy(alpha = 0.9f),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    } else {
        Surface(
            modifier = modifier,
            color = Color.DarkGray.copy(alpha = 0.8f),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "🖐️ Show your hand to the camera",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun PermissionDeniedScreen(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📷",
                fontSize = 64.sp
            )
            Text(
                text = "Camera Permission Required",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "This feature needs camera access to track your hand gestures",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRequestPermission) {
                Text("Grant Permission")
            }
        }
    }
}
