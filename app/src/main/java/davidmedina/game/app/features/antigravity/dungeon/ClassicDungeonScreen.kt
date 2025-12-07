package davidmedina.game.app.features.antigravity.dungeon

import davidmedina.game.app.features.antigravity.dungeon.model.*
import davidmedina.game.app.features.antigravity.dungeon.ui.*

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import davidmedina.game.app.ui.composables.TDMButton
import davidmedina.game.app.ui.composables.gameBoxBackground

@Composable
fun ClassicDungeonScreen(viewModel: ClassicDungeonViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .gameBoxBackground()
    ) {
        when (state.gameState) {
            DungeonGameState.CLASS_SELECTION -> ClassSelectionScreen(viewModel)
            DungeonGameState.EXPLORING -> ExploringView(state, viewModel)
            DungeonGameState.IN_COMBAT -> CombatView(state, viewModel)
            DungeonGameState.INVENTORY -> InventoryView(state, viewModel)
            DungeonGameState.GAME_OVER -> GameOverView(state, viewModel)
            DungeonGameState.LEVEL_UP -> LevelUpView(state, viewModel)
            DungeonGameState.VICTORY -> VictoryView(state, viewModel)
            DungeonGameState.SHOPPING -> ShopView(state, viewModel)
            DungeonGameState.RESTING -> InnView(state, viewModel)
            DungeonGameState.DEBUG_MENU -> DebugMenuView(state, viewModel)
        }
    }
    
    // Start game on first launch
    LaunchedEffect(Unit) {
        if (state.tiles.isEmpty()) {
            viewModel.startNewGame()
        }
    }
}

@Composable
fun ExploringView(state: ClassicDungeonState, viewModel: ClassicDungeonViewModel) {
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Abort Simulation?") },
            text = { Text("Do you wish to exit the current run? Progress will be lost.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.exitToMenu()
                        showExitDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Exit Run")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF222222),
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Improved Header with Speed Controls
        DungeonHeader(state, viewModel)

        // Dungeon Viewport (Centered)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black), // Dark background for the void
            contentAlignment = Alignment.Center
        ) {
            // Frame for the Dungeon
            Box(
                modifier = Modifier
                    .size(340.dp) // Covers 11x30dp = 330dp + border margin
                    .border(2.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
                    .padding(4.dp)
            ) {
                 DungeonCanvas(state)
            }
            
            // Floating message log for better visibility
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                 MessageLog(state.messages.takeLast(3))
            }
        }

        // Controls
        ControlPanel(viewModel, state)
    }
}

@Composable
fun DungeonHeader(state: ClassicDungeonState, viewModel: ClassicDungeonViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        border = BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Row 1: Title, Level, Speed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(state.player.playerClass.emoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Level ${state.dungeonLevel}", color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Speed Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(Color(0xFF111111), RoundedCornerShape(16.dp))
                ) {
                    listOf(1f, 2f, 5f).forEach { speed ->
                        val isSelected = state.gameSpeed == speed
                        Box(
                            modifier = Modifier
                                .clickable { viewModel.setGameSpeed(speed) }
                                .background(
                                    if (isSelected) Color(0xFF2196F3) else Color.Transparent,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "${speed.toInt()}x",
                                color = if (isSelected) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: HP / MP Bars
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                 // HP
                 Column(modifier = Modifier.weight(1f)) {
                     Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                         Text("HP", color = Color(0xFFFF5252), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                         Text("${state.player.hp}/${state.player.maxHp}", color = Color.White, fontSize = 10.sp)
                     }
                     LinearProgressIndicator(
                         progress = state.player.hp.toFloat() / state.player.maxHp,
                         modifier = Modifier.fillMaxWidth().height(8.dp).padding(top=4.dp),
                         color = Color(0xFFFF5252),
                         trackColor = Color(0xFF442222)
                     )
                 }

                 // MP
                 Column(modifier = Modifier.weight(1f)) {
                     Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                         Text("MP", color = Color(0xFF448AFF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                         Text("${state.player.mana}/${state.player.maxMana}", color = Color.White, fontSize = 10.sp)
                     }
                     LinearProgressIndicator(
                         progress = state.player.mana.toFloat() / state.player.maxMana,
                         modifier = Modifier.fillMaxWidth().height(8.dp).padding(top=4.dp),
                         color = Color(0xFF448AFF),
                         trackColor = Color(0xFF222244)
                     )
                 }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Row 3: Stats Summary
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("⚔️ ${state.player.getTotalAttack()}", color = Color.Yellow, fontSize = 12.sp)
                Text("🛡️ ${state.player.getTotalDefense()}", color = Color.Green, fontSize = 12.sp)
                Text("✨ ${state.player.getTotalMagic()}", color = Color.Magenta, fontSize = 12.sp)
                Text("💰 ${state.player.gold}g", color = Color.Yellow, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun StatsBar(player: Player) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("⚔️ Lv.${player.level}", color = Color.Yellow, fontWeight = FontWeight.Bold)
                Text("💰 ${player.gold}g", color = Color.Yellow, fontWeight = FontWeight.Bold)
                Text("📊 XP: ${player.xp}/${player.xpToNextLevel}", color = Color.Yellow, fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                // HP Bar
                Column(modifier = Modifier.weight(1f)) {
                    Text("❤️ HP", color = Color.White, fontSize = 10.sp)
                    LinearProgressIndicator(
                        progress = player.hp.toFloat() / player.maxHp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = Color.Red,
                        trackColor = Color.DarkGray
                    )
                    Text("${player.hp}/${player.maxHp}", color = Color.White, fontSize = 10.sp)
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Mana Bar
                Column(modifier = Modifier.weight(1f)) {
                    Text("💙 MP", color = Color.White, fontSize = 10.sp)
                    LinearProgressIndicator(
                        progress = player.mana.toFloat() / player.maxMana,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = Color.Blue,
                        trackColor = Color.DarkGray
                    )
                    Text("${player.mana}/${player.maxMana}", color = Color.White, fontSize = 10.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("⚔️ ATK: ${player.getTotalAttack()}", color = Color.Cyan, fontSize = 12.sp)
                Text("🛡️ DEF: ${player.getTotalDefense()}", color = Color.Cyan, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DungeonCanvas(state: ClassicDungeonState) {
    val tileSize = 30.dp
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val tileSizePx = tileSize.toPx()
        val viewportSize = state.viewportSize
        val centerX = viewportSize / 2
        val centerY = viewportSize / 2
        
        // Calculate visible area centered on player
        val startX = (state.player.position.x - centerX).coerceAtLeast(0)
        val startY = (state.player.position.y - centerY).coerceAtLeast(0)
        val endX = (startX + viewportSize).coerceAtMost(state.gridWidth)
        val endY = (startY + viewportSize).coerceAtMost(state.gridHeight)
        
        // Draw tiles
        for (y in startY until endY) {
            for (x in startX until endX) {
                val tile = state.tiles.find { it.position.x == x && it.position.y == y }
                if (tile != null && tile.isRevealed) {
                    val screenX = (x - startX) * tileSizePx
                    val screenY = (y - startY) * tileSizePx
                    
                    val color = when {
                        !tile.isVisible -> when (tile.type) {
                            TileType.FLOOR -> Color.DarkGray.copy(alpha = 0.5f)
                            TileType.WALL -> Color.Gray.copy(alpha = 0.5f)
                            TileType.STAIRS_DOWN -> Color.Yellow.copy(alpha = 0.5f)
                            TileType.HEALING_FOUNTAIN -> Color.Cyan.copy(alpha = 0.5f)
                            TileType.SHOP -> Color(0xFFFFD700).copy(alpha = 0.5f)
                            TileType.INN -> Color(0xFFFF69B4).copy(alpha = 0.5f)
                            else -> Color.Gray.copy(alpha = 0.5f)
                        }
                        else -> when (tile.type) {
                            TileType.FLOOR -> Color(0xFF2a2a2a)
                            TileType.WALL -> Color(0xFF555555)
                            TileType.STAIRS_DOWN -> Color.Yellow
                            TileType.DOOR -> Color(0xFF8B4513)
                            TileType.STAIRS_UP -> Color.Cyan
                            TileType.HEALING_FOUNTAIN -> Color(0xFF00FFFF) // Bright cyan
                            TileType.SHOP -> Color(0xFFFFD700) // Gold for Shop
                            TileType.INN -> Color(0xFFFF69B4) // Hot Pink for Inn
                            TileType.EMPTY -> Color(0xFF000000)
                        }
                    }
                    
                    drawRect(
                        color = color,
                        topLeft = Offset(screenX, screenY),
                        size = Size(tileSizePx, tileSizePx)
                    )
                    
                    // Draw grid lines
                    drawRect(
                        color = Color.Black.copy(alpha = 0.3f),
                        topLeft = Offset(screenX, screenY),
                        size = Size(tileSizePx, tileSizePx),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                    )
                }
            }
        }
        
        // Draw items
        state.items.forEach { item ->
            val itemPos = item.position
            if (itemPos != null) {
                val tile = state.tiles.find { it.position == itemPos && it.isVisible }
                if (tile != null && itemPos.x in startX until endX && itemPos.y in startY until endY) {
                    val screenX = (itemPos.x - startX) * tileSizePx + tileSizePx / 2
                    val screenY = (itemPos.y - startY) * tileSizePx + tileSizePx / 2
                    
                    // Color based on rarity
                    val itemColor = when (item.rarity) {
                        "Legendary" -> Color(0xFFFFD700) // Gold
                        "Epic" -> Color(0xFFAA00FF) // Purple
                        "Rare" -> Color(0xFF0088FF) // Blue
                        else -> Color(0xFFCCCCCC) // Gray
                    }
                    
                    drawCircle(
                        color = itemColor,
                        radius = tileSizePx / 4,
                        center = Offset(screenX, screenY)
                    )
                }
            }
        }
        
        // Draw enemies
        state.enemies.filter { it.isAlive }.forEach { enemy ->
            val tile = state.tiles.find { it.position == enemy.position && it.isVisible }
            if (tile != null) {
                val x = enemy.position.x
                val y = enemy.position.y
                
                if (x in startX until endX && y in startY until endY) {
                    val screenX = (x - startX) * tileSizePx + tileSizePx / 2
                    val screenY = (y - startY) * tileSizePx + tileSizePx / 2
                    
                    val color = when (enemy.name) {
                        "Rat" -> Color(0xFF8B4513)
                        "Goblin" -> Color(0xFF00FF00)
                        "Skeleton" -> Color.White
                        "Orc" -> Color(0xFFFF6B6B)
                        "Dragon" -> Color.Magenta
                        else -> Color.Red
                    }
                    
                    drawCircle(
                        color = color,
                        radius = tileSizePx / 3,
                        center = Offset(screenX, screenY)
                    )
                    
                    // HP bar above enemy
                    val hpBarWidth = tileSizePx * 0.8f
                    val hpBarHeight = 4f
                    val hpPercent = enemy.hp.toFloat() / enemy.maxHp
                    
                    drawRect(
                        color = Color.DarkGray,
                        topLeft = Offset(screenX - hpBarWidth / 2, screenY - tileSizePx / 2),
                        size = Size(hpBarWidth, hpBarHeight)
                    )
                    drawRect(
                        color = Color.Red,
                        topLeft = Offset(screenX - hpBarWidth / 2, screenY - tileSizePx / 2),
                        size = Size(hpBarWidth * hpPercent, hpBarHeight)
                    )
                }
            }
        }
        
        // Draw player
        val playerX = (state.player.position.x - startX) * tileSizePx + tileSizePx / 2
        val playerY = (state.player.position.y - startY) * tileSizePx + tileSizePx / 2
        
        drawCircle(
            color = Color.Cyan,
            radius = tileSizePx / 2.5f,
            center = Offset(playerX, playerY)
        )
        
        // Player indicator
        drawCircle(
            color = Color.Yellow.copy(alpha = 0.3f),
            radius = tileSizePx / 1.8f,
            center = Offset(playerX, playerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
    }
}

@Composable
fun MessageLog(messages: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.padding(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ControlPanel(viewModel: ClassicDungeonViewModel, state: ClassicDungeonState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Toggle Handle
            Icon(
                imageVector = if (state.isControlsExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = "Toggle Controls",
                tint = Color.Gray,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { viewModel.toggleAutoPlay() }
                    .padding(4.dp)
            )

            AnimatedVisibility(visible = state.isControlsExpanded) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // D-Pad
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Up
                        DirectionalButton("▲", enabled = !state.isAutoPlaying) { 
                            viewModel.movePlayer(GridPosition(0, -1)) 
                        }
                        
                        Row {
                            // Left
                            DirectionalButton("◀", enabled = !state.isAutoPlaying) { 
                                viewModel.movePlayer(GridPosition(-1, 0)) 
                            }
                            
                            Spacer(modifier = Modifier.width(64.dp))
                            
                            // Right
                            DirectionalButton("▶", enabled = !state.isAutoPlaying) { 
                                viewModel.movePlayer(GridPosition(1, 0)) 
                            }
                        }
                        
                        // Down
                        DirectionalButton("▼", enabled = !state.isAutoPlaying) { 
                            viewModel.movePlayer(GridPosition(0, 1)) 
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { viewModel.toggleInventory() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("🎒 Bag")
                        }
                        
                        Button(
                            onClick = { viewModel.toggleAutoPlay() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isAutoPlaying) Color(0xFFFF6B6B) else Color(0xFF2196F3)
                            )
                        ) {
                            Text(if (state.isAutoPlaying) "🤖 Stop AI" else "🤖 Auto-Play")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar(state: ClassicDungeonState, viewModel: ClassicDungeonViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Lvl ${state.dungeonLevel} - Turn ${state.turnCount}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            // Speed Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Speed: ", color = Color.Gray, fontSize = 12.sp)
                listOf(1f, 2f, 5f).forEach { speed ->
                    Text(
                        "${speed.toInt()}x",
                        color = if (state.gameSpeed == speed) Color.Yellow else Color.Gray,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable { viewModel.setGameSpeed(speed) },
                        fontWeight = if (state.gameSpeed == speed) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        
        Row {
            Button(
                onClick = { viewModel.toggleInventory() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("🎒 Inv")
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = { viewModel.toggleAutoPlay() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isAutoPlaying) Color.Red else Color.Green
                )
            ) {
                Text(if (state.isAutoPlaying) "🛑 Stop" else "🤖 Auto")
            }
        }
    }
}

@Composable
fun DirectionalButton(symbol: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
    ) {
        Text(symbol, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CombatView(state: ClassicDungeonState, viewModel: ClassicDungeonViewModel) {
    val enemy = state.currentEnemy ?: return
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        StatsBar(state.player)
        
        // Enemy visualization with Canvas drawing
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.9f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "⚔️ COMBAT!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Canvas-drawn enemy
                EnemyCanvas(enemy = enemy,  modifier = Modifier.height(180.dp).fillMaxWidth())
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("${enemy.emoji} ${enemy.name}", fontSize = 24.sp, color = Color.White)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = enemy.hp.toFloat() / enemy.maxHp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    color = Color.Green,
                    trackColor = Color.DarkGray
                )
                Text("HP: ${enemy.hp}/${enemy.maxHp}", color = Color.White, fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row {
                    Text("⚔️ ${enemy.attack}", color = Color.Yellow, modifier = Modifier.padding(horizontal = 8.dp))
                    Text("🛡️ ${enemy.defense}", color = Color.Yellow, modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }
        
        MessageLog(state.messages)
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Row 1
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.attack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("⚔️ Attack")
                    }
                    Button(
                        onClick = { viewModel.defend() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🛡️ Defend")
                    }
                }
                
                // Row 2
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val (abilityName, cost) = state.player.playerClass.getAbilityInfo()
                    Button(
                        onClick = { viewModel.useAbility() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("⚡ $abilityName ($cost)")
                    }
                    
                    Button(
                        onClick = { viewModel.toggleInventory() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🎒 Item", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryView(state: ClassicDungeonState, viewModel: ClassicDungeonViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Black.copy(alpha = 0.9f))
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("👤 CHARACTER SHEET", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Yellow)
            IconButton(onClick = { viewModel.toggleInventory() }) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            // Left Side: Character Paper Doll
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Draw Character Silhouette
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val scale = size.minDimension / 400f // Scaling factor

                        // Head
                        drawCircle(
                            color = Color.Gray,
                            radius = 40f * scale,
                            center = Offset(centerX, centerY - 120f * scale),
                            style = Stroke(width = 4f)
                        )
                        // Body
                        drawLine(
                            color = Color.Gray,
                            start = Offset(centerX, centerY - 80f * scale),
                            end = Offset(centerX, centerY + 80f * scale),
                            strokeWidth = 4f
                        )
                        // Arms
                        drawLine(
                            color = Color.Gray,
                            start = Offset(centerX - 80f * scale, centerY - 40f * scale),
                            end = Offset(centerX + 80f * scale, centerY - 40f * scale),
                            strokeWidth = 4f
                        )
                        // Legs
                        drawLine(
                            color = Color.Gray,
                            start = Offset(centerX, centerY + 80f * scale),
                            end = Offset(centerX - 60f * scale, centerY + 200f * scale),
                            strokeWidth = 4f
                        )
                        drawLine(
                            color = Color.Gray,
                            start = Offset(centerX, centerY + 80f * scale),
                            end = Offset(centerX + 60f * scale, centerY + 200f * scale),
                            strokeWidth = 4f
                        )
                    }

                    // Equipment Slots overlay
                    // Weapon (Right Hand - Left side of screen)
                    // Helmet (Head - Top)
                    EquipmentSlot(
                        item = state.player.equippedHelmet,
                        slotName = "Head",
                        onClick = { viewModel.unequipItem(ItemType.HELMET) },
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                    )

                    // Weapon (Left)
                    EquipmentSlot(
                        item = state.player.equippedWeapon,
                        slotName = "Main Hand",
                        onClick = { viewModel.unequipItem(ItemType.WEAPON) },
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    // Armor (Body - Center)
                    EquipmentSlot(
                        item = state.player.equippedArmor,
                        slotName = "Body",
                        onClick = { viewModel.unequipItem(ItemType.ARMOR) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                    
                    // Accessory (Right)
                    EquipmentSlot(
                        item = state.player.equippedAccessory,
                        slotName = "Acc.",
                        onClick = { viewModel.unequipItem(ItemType.ACCESSORY) },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )

                    // Boots (Legs - Bottom)
                    EquipmentSlot(
                        item = state.player.equippedBoots,
                        slotName = "Legs",
                        onClick = { viewModel.unequipItem(ItemType.BOOTS) },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                    )
                }

                // Stats Summary
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("STATS", color = Color.Cyan, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("ATK: ${state.player.getTotalAttack()}", color = Color.White)
                                Text("DEF: ${state.player.getTotalDefense()}", color = Color.White)
                                Text("MAG: ${state.player.getTotalMagic()}", color = Color.Magenta)
                            }
                            Column {
                                Text("HP: ${state.player.hp}/${state.player.maxHp}", color = Color.Green)
                                Text("MP: ${state.player.mana}/${state.player.maxMana}", color = Color.Blue)
                                Text("CRIT: ${(state.player.getTotalCrit() * 100).toInt()}%", color = Color.Yellow)
                                Text("DODGE: ${(state.player.getTotalDodge() * 100).toInt()}%", color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right Side: Inventory List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF111111), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text("📦 BACKPACK", color = Color.Yellow, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.player.inventory) { item ->
                        InventoryItemCard(item = item) {
                            when (item.type) {
                                ItemType.WEAPON, ItemType.ARMOR, ItemType.HELMET, ItemType.BOOTS, ItemType.ACCESSORY -> viewModel.equipItem(item)
                                ItemType.POTION -> viewModel.useItem(item)
                                else -> {}
                            }
                        }
                    }
                    if (state.player.inventory.isEmpty()) {
                        item {
                            Text("Empty...", color = Color.Gray, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentSlot(
    item: Item?,
    slotName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .border(2.dp, if (item != null) Color.Yellow else Color.Gray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (item != null) {
                Text(item.emoji, fontSize = 32.sp)
            } else {
                Text(slotName.take(1), color = Color.Gray, fontSize = 24.sp)
            }
        }
        Text(
            text = item?.name ?: slotName,
            color = if (item != null) Color.White else Color.Gray,
            fontSize = 10.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(64.dp)
        )
    }
}

@Composable
fun InventoryItemCard(item: Item, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black, RoundedCornerShape(4.dp))
                    .border(1.dp, getRarityColor(item.rarity), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.emoji, fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, color = getRarityColor(item.rarity), fontWeight = FontWeight.Bold)
                val stats = when(item.type) {
                    ItemType.WEAPON -> "ATK +${item.attack}"
                    ItemType.ARMOR, ItemType.HELMET, ItemType.BOOTS -> "DEF +${item.defense}"
                    ItemType.ACCESSORY -> "MAG +${item.magic}"
                    ItemType.POTION -> "Heals ${item.healing} HP"
                    else -> "Val: ${item.value}g"
                }
                
                // Add extra info for crit/dodge
                val extras = mutableListOf<String>()
                if (item.critChance > 0) extras.add("Crit +${(item.critChance*100).toInt()}%")
                if (item.dodgeChance > 0) extras.add("Dodge +${(item.dodgeChance*100).toInt()}%")
                if (extras.isNotEmpty()) {
                    Text(extras.joinToString(" "), color = Color.Yellow, fontSize = 10.sp)
                }
                
                Text(stats, color = Color.Gray, fontSize = 12.sp)
            }
            
            Button(
                onClick = onClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = when(item.type) {
                        ItemType.WEAPON, ItemType.ARMOR, ItemType.HELMET, ItemType.BOOTS, ItemType.ACCESSORY -> "Equip"
                        ItemType.POTION -> "Use"
                        else -> "Info"
                    },
                    fontSize = 10.sp
                )
            }
        }
    }
}

fun getRarityColor(rarity: String): Color {
    return when (rarity) {
        "Legendary" -> Color(0xFFFFD700)
        "Epic" -> Color(0xFFAA00FF)
        "Rare" -> Color(0xFF0088FF)
        else -> Color.White
    }
}

@Composable
fun LevelUpView(state: ClassicDungeonState, viewModel: ClassicDungeonViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700).copy(alpha = 0.95f)),
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "⭐ LEVEL UP! ⭐",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "You are now level ${state.player.level}!",
                    fontSize = 20.sp,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Stats Increased:", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("HP: +20", color = Color.Black)
                Text("Mana: +10", color = Color.Black)
                Text("Attack: +3", color = Color.Black)
                Text("Defense: +2", color = Color.Black)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(onClick = { viewModel.continuePlaying() }) {
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
fun GameOverView(state: ClassicDungeonState, viewModel: ClassicDungeonViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.95f)),
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "💀 GAME OVER 💀",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("You reached dungeon level ${state.dungeonLevel}", color = Color.White)
                Text("Final Level: ${state.player.level}", color = Color.White)
                Text("Gold Collected: ${state.player.gold}", color = Color.White)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(onClick = { viewModel.startNewGame() }) {
                    Text("Try Again")
                }
            }
        }
    }
}

@Composable
fun VictoryView(state: ClassicDungeonState, viewModel: ClassicDungeonViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Green.copy(alpha = 0.95f)),
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🎉 VICTORY! 🎉",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(onClick = { viewModel.startNewGame() }) {
                    Text("Play Again")
                }
            }
        }
    }
}
@Composable
fun ClassSelectionScreen(viewModel: ClassicDungeonViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "INITIATE SEQUENCE",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Green,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            "Select Avatar for Simulation",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(PlayerClass.values()) { playerClass ->
                val (abilityName, cost) = playerClass.getAbilityInfo()
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.chooseClass(playerClass) },
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                    border = BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = playerClass.emoji,
                            fontSize = 40.sp,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Column {
                            Text(
                                text = playerClass.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = playerClass.description,
                                fontSize = 14.sp,
                                color = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⚡ Ability: $abilityName ($cost MP)",
                                fontSize = 12.sp,
                                color = Color.Magenta,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.openDebugMenu() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("🔧 Debug Mode")
        }
    }

