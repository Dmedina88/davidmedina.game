package davidmedina.game.app.ui.composables


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import davidmedina.game.app.R
import davidmedina.game.app.ui.theme.Pink80

@Preview
@Composable
fun SideInfoBar(
    turnClicked: (() -> Unit) = {}
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
        //enamy life
        Text(text = "20 /20")
        Image(
            painter = painterResource(R.drawable.ic_baseline_battery_0_bar_24),
            contentDescription = "energy icon"
        )

        Button(onClick = { turnClicked() }, Modifier.background(Color.Red, CircleShape)) {
            Text(text = "Turn")
        }


        Image(
            painter = painterResource(R.drawable.ic_baseline_battery_0_bar_24),
            contentDescription = "energy icon"
        )

        Text(text = "Life")
        // user life
        Text(text = "20 /20")

    }
}

