package davidmedina.game.app.features.antigravity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DebugMenuView(state: ClassicDungeonState, viewModel: ClassicDungeonViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF222222)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔧 DEBUG CONTROL DECK", fontSize = 24.sp, color = Color.Green, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        
        DebugButton("💰 Give 1000 Gold") { viewModel.debugGiveGold(1000) }
        DebugButton("🏬 Force Shop") { viewModel.debugForceShop() }
        DebugButton("🏨 Force Inn") { viewModel.debugForceInn() }
        DebugButton("🆙 Force Level Up") { viewModel.debugLevelUp() }
        DebugButton("👾 Spawn Enemy") { viewModel.debugSpawnEnemy() }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { viewModel.exitToMenu() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Exit Debug Mode")
        }
    }
}

@Composable
fun DebugButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
    ) {
        Text(text, color = Color.White)
    }
}
