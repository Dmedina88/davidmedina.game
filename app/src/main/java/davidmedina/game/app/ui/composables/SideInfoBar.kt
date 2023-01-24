package davidmedina.game.app.ui.composables


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import davidmedina.game.app.R
import davidmedina.game.app.ui.theme.Pink80
import davidmedina.game.app.viewmodel.GameState

@Composable
fun SideInfoBar(
    state: GameState,
    turnClicked: () -> Unit = {}
) {
    Column(
        Modifier
            .background(Pink80)
            .fillMaxHeight()
            .fillMaxWidth(.10f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(text = "Life")
        Text(text = "${state.opponent.life} /20")
        EnergyBar(state.opponent.energy)

        Button(onClick = { turnClicked() }, Modifier.background(Color.Red, CircleShape)) {
            Text(text = "Turn")
        }

        EnergyBar(state.player.energy)
        Text(text = "Life")
        Text(text = "${state.player.life} /20")
    }
}

@Composable
fun EnergyBar(energy: Int){
    val icon = when (energy){
        0-> R.drawable.ic_baseline_battery_0_bar_24
        1-> R.drawable.ic_baseline_battery_1_bar_24
        2-> R.drawable.ic_baseline_battery_2_bar_24
        3-> R.drawable.ic_baseline_battery_3_bar_24
        4-> R.drawable.ic_baseline_battery_4_bar_24
        5-> R.drawable.ic_baseline_battery_5_bar_24
        6-> R.drawable.ic_baseline_battery_5_bar_24
        else -> R.drawable.ic_baseline_battery_full_24
    }
    Image(
        painter = painterResource(icon),
        contentDescription = "energy icon"
    )
    Text(text = energy.toString())
}


