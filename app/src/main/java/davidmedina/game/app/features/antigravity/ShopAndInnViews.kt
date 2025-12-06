package davidmedina.game.app.features.antigravity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShopView(state: ClassicDungeonState, viewModel: ClassicDungeonViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF221100)), // Brownish shop background
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("GENERAL STORE", fontSize = 32.sp, color = Color.Yellow, fontWeight = FontWeight.Bold)
        Text("Gold: ${state.player.gold}g", fontSize = 20.sp, color = Color.White)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.weight(1f)) {
            // Shop Inventory
            Column(modifier = Modifier.weight(1f).padding(8.dp)) {
                Text("BUY", color = Color.Green, fontWeight = FontWeight.Bold)
                LazyColumn {
                    items(state.shopItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .clickable { viewModel.buyItem(item) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF443311))
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(item.emoji, fontSize = 24.sp)
                                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                                    Text(item.name, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text("${item.value}g", color = Color.Yellow)
                                }
                                Text("BUY", color = Color.Green)
                            }
                        }
                    }
                }
            }
            
            // Player Inventory (Sell)
            Column(modifier = Modifier.weight(1f).padding(8.dp)) {
                Text("SELL (50% Value)", color = Color.Red, fontWeight = FontWeight.Bold)
                LazyColumn {
                    items(state.player.inventory) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .clickable { viewModel.sellItem(item) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(item.emoji, fontSize = 24.sp)
                                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                                    Text(item.name, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text("${item.value/2}g", color = Color.Yellow)
                                }
                                Text("SELL", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
        
        Button(
            onClick = { viewModel.leaveBuilding() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Leave Shop")
        }
    }
}

@Composable
fun InnView(state: ClassicDungeonState, viewModel: ClassicDungeonViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF001122)), // Dark blue Inn background
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("THE COZY INN", fontSize = 32.sp, color = Color.Cyan, fontWeight = FontWeight.Bold)
        Text("Rest your weary bones...", fontSize = 16.sp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Gold: ${state.player.gold}g", fontSize = 24.sp, color = Color.Yellow)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        InnOptionCard("Basic Rest", "10g", "Restore 50% HP/MP") { viewModel.restAtInn("Basic") }
        InnOptionCard("Luxury Suite", "50g", "Full Heal + 10% Stats Buff (50 turns)") { viewModel.restAtInn("Luxury") }
        InnOptionCard("Warrior's Feast", "100g", "Full Heal + 30% Attack Buff (30 turns)") { viewModel.restAtInn("Feast") }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { viewModel.leaveBuilding() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Leave Inn")
        }
    }
}

@Composable
fun InnOptionCard(title: String, cost: String, desc: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF112233)),
        border = BorderStroke(1.dp, Color.Cyan)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(desc, color = Color.Gray, fontSize = 14.sp)
            }
            Text(cost, color = Color.Yellow, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}
