package davidmedina.game.app.features.antigravity

import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import davidmedina.game.app.ui.composables.TDMButton
import davidmedina.game.app.ui.composables.gameBoxBackground

@Composable
fun HandQuestScreen(
    navController: NavController,
    viewModel: HandQuestViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == 
            PackageManager.PERMISSION_GRANTED
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            viewModel.startGame()
        }
    }
    val handTrackingManager = remember { HandTrackingManager(context) }
    
    val gameState by viewModel.state.collectAsState()
    val handState by handTrackingManager.handState.collectAsState()
    
    // Update hand state in viewmodel
    LaunchedEffect(handState) {
        viewModel.updateHandState(handState)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            handTrackingManager.shutdown()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .gameBoxBackground()
    ) {
        // Camera preview in background
        if (gameState.gameState != GameState.IDLE) {
            if (hasCameraPermission) {
                CameraPreview(
                    handTrackingManager = handTrackingManager,
                    lifecycleOwner = lifecycleOwner
                )
            }
        }
        
        // Game canvas
        if (gameState.gameState != GameState.IDLE) {
            GameCanvas(
                gameState = gameState,
                handState = handState,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // UI Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top HUD
            TopHUD(gameState)
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bottom UI
            when (gameState.gameState) {
                GameState.IDLE -> {
                    StartScreen(
                        onStart = { 
                            if (hasCameraPermission) {
                                viewModel.startGame()
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        hasCameraPermission = hasCameraPermission,
                        onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                    )
                }
                GameState.IN_COMBAT -> {
                    CombatUI(gameState, handState)
                }
                GameState.EXPLORING -> {
                    ExploringUI(gameState)
                }
                GameState.VICTORY, GameState.DEFEAT -> {
                    ResultUI(gameState, onRestart = { viewModel.startGame() })
                }
            }
            
            // Combat log
            if (gameState.gameState != GameState.IDLE) {
                CombatLogUI(gameState.combatLogs)
            }
        }
    }
}

@Composable
fun CameraPreview(
    handTrackingManager: HandTrackingManager,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner
) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                
                post {
                    handTrackingManager.updateScreenSize(width.toFloat(), height.toFloat())
                    handTrackingManager.startCamera(lifecycleOwner, this)
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    )
}

@Composable
fun GameCanvas(
    gameState: HandQuestState,
    handState: HandState,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        // Draw character
        val charPos = gameState.character.position
        drawCircle(
            color = Color.Cyan,
            radius = 40f,
            center = charPos,
            style = Stroke(width = 4f)
        )
        drawCircle(
            color = Color.Cyan.copy(alpha = 0.3f),
            radius = 40f,
            center = charPos
        )
        
        // Draw hand tracking indicator
        if (handState.isDetected) {
            drawCircle(
                color = Color.Yellow.copy(alpha = 0.5f),
                radius = 20f,
                center = handState.position
            )
        }
        
        // Draw enemies
        gameState.enemies.forEach { enemy ->
            if (enemy.isAlive) {
                val color = when (enemy.type) {
                    EnemyType.GOBLIN -> Color.Red
                    EnemyType.SKELETON -> Color.White
                    EnemyType.DRAGON -> Color.Magenta
                    EnemyType.SLIME -> Color.Green
                }
                
                drawCircle(
                    color = color,
                    radius = 50f,
                    center = enemy.position,
                    style = Stroke(width = 4f)
                )
                drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = 50f,
                    center = enemy.position
                )
                
                // HP bar above enemy
                val barWidth = 80f
                val barHeight = 8f
                val hpPercentage = enemy.hp.toFloat() / enemy.maxHp
                
                drawRect(
                    color = Color.DarkGray,
                    topLeft = Offset(enemy.position.x - barWidth/2, enemy.position.y - 70f),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                )
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(enemy.position.x - barWidth/2, enemy.position.y - 70f),
                    size = androidx.compose.ui.geometry.Size(barWidth * hpPercentage, barHeight)
                )
            }
        }
        
        // Draw loot
        gameState.loot.forEach { loot ->
            if (!loot.isCollected) {
                drawCircle(
                    color = Color.Yellow,
                    radius = 25f,
                    center = loot.position,
                    style = Stroke(width = 3f)
                )
                drawCircle(
                    color = Color.Yellow.copy(alpha = 0.2f),
                    radius = 25f,
                    center = loot.position
                )
            }
        }
    }
}

@Composable
fun TopHUD(gameState: HandQuestState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⚔️ Level ${gameState.character.level}",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "🏆 Score: ${gameState.score}",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "🏰 Floor ${gameState.dungeonLevel}",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // HP Bar
            ProgressBar(
                label = "HP",
                current = gameState.character.hp,
                max = gameState.character.maxHp,
                color = Color.Red,
                emoji = "❤️"
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Mana Bar
            ProgressBar(
                label = "Mana",
                current = gameState.character.mana,
                max = gameState.character.maxMana,
                color = Color.Blue,
                emoji = "💙"
            )
        }
    }
}

@Composable
fun ProgressBar(
    label: String,
    current: Int,
    max: Int,
    color: Color,
    emoji: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$emoji $label",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.width(70.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(16.dp)
                .background(Color.DarkGray, RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(current.toFloat() / max)
                    .background(color, RoundedCornerShape(8.dp))
            )
        }
        Text(
            text = "$current/$max",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun StartScreen(
    onStart: () -> Unit,
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎮 HAND QUEST",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Yellow,
            fontWeight = FontWeight.Bold,
            fontSize = 48.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "ML-Powered Gesture RPG",
            color = Color.White,
            fontSize = 20.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "📋 How to Play:",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("✋ Move your hand to control character", color = Color.White)
                Text("🤏 PINCH to attack enemies", color = Color.White)
                Text("🖐️ OPEN PALM to defend (restore mana)", color = Color.White)
                Text("✨ Combo: Pinch→Palm→Pinch = Special Attack!", color = Color.White)
                Text("💎 Collect loot by pinching near it", color = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (!hasCameraPermission) {
            TDMButton(text = "Grant Camera Permission") {
                onRequestPermission()
            }
        } else {
            TDMButton(text = "START ADVENTURE") {
                onStart()
            }
        }
    }
}

@Composable
fun CombatUI(gameState: HandQuestState, handState: HandState) {
    val enemy = gameState.currentEnemy ?: return
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚔️ COMBAT MODE",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${enemy.type.emoji} ${enemy.name}",
                fontSize = 32.sp,
                color = Color.White
            )
            
            Text(
                text = "HP: ${enemy.hp}/${enemy.maxHp}",
                fontSize = 18.sp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Gesture indicator
            val gestureText = when (handState.gesture) {
                GestureType.PINCH -> "🤏 ATTACKING!"
                GestureType.OPEN_PALM -> "🛡️ DEFENDING!"
                GestureType.IDLE -> "✋ Ready..."
            }
            
            Text(
                text = gestureText,
                fontSize = 20.sp,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold
            )
            
            // Combo display
            if (gameState.gestureCombo.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Combo: ${gameState.gestureCombo.joinToString("→") { 
                        when(it) {
                            GestureType.PINCH -> "🤏"
                            GestureType.OPEN_PALM -> "🖐️"
                            GestureType.IDLE -> "⭕"
                        }
                    }}",
                    fontSize = 16.sp,
                    color = Color.Cyan
                )
            }
        }
    }
}

@Composable
fun ExploringUI(gameState: HandQuestState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🗺️ EXPLORING",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Green
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "👺 Enemies: ${gameState.enemies.count { it.isAlive }}",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "💎 Loot: ${gameState.loot.count { !it.isCollected }}",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ResultUI(gameState: HandQuestState, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (gameState.gameState == GameState.VICTORY) 
                    Color.Green.copy(alpha = 0.9f) 
                else 
                    Color.Red.copy(alpha = 0.9f)
            ),
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (gameState.gameState == GameState.VICTORY) "🎉 VICTORY!" else "💀 DEFEATED",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Final Score: ${gameState.score}",
                    fontSize = 24.sp,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                TDMButton(text = "Play Again") {
                    onRestart()
                }
            }
        }
    }
}

@Composable
fun CombatLogUI(logs: List<CombatLog>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.padding(8.dp),
            reverseLayout = true
        ) {
            items(logs.reversed()) { log ->
                Text(
                    text = log.message,
                    color = if (log.isPlayerAction) Color.Cyan else Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
