package davidmedina.game.app.features.antigravity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import davidmedina.game.app.Routes
import davidmedina.game.app.ui.composables.TDMButton
import davidmedina.game.app.ui.composables.gameBoxBackground

@Composable
fun AntiGravityMenuScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .gameBoxBackground()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "AntiGravity Hub",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            
            TDMButton(text = "Gravity Playground") {
                navController.navigate(Routes.GRAVITY_PLAYGROUND.name)
            }
            
            TDMButton(text = "Color Mosaic") {
                navController.navigate(Routes.COLOR_MOSAIC.name)
            }
            
            // Placeholder for future features
            Text(
                text = "More coming soon...",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
