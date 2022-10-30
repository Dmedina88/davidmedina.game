package davidmedina.game.app.ui.screens

import android.content.pm.ActivityInfo
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.ui.composables.CardState
import davidmedina.game.app.ui.composables.DMGCard
import davidmedina.game.app.ui.composables.LockScreenOrientation
import davidmedina.game.app.ui.composables.mockCardState
import davidmedina.game.app.ui.theme.Pink80
import davidmedina.game.app.ui.theme.Purple40
import davidmedina.game.app.ui.theme.PurpleGrey80

@Preview
@Composable
fun GameScreen() {
  Row(
    Modifier.fillMaxSize(),
  ) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)


    SideInfoBar()
    Column {
      //opponet
      Row(
        Modifier
          .background(Color.Green)
          .padding(16.dp)
      ) {
        //failt Spot
        Box(
          Modifier
            .background(Purple40)
            .width(150.dp)
            .height(200.dp)
        ) {

        }

        Box(
          Modifier
            .background(PurpleGrey80)
            .width(150.dp)
            .height(200.dp)
        ) {

        }
        Box(
          Modifier
            .background(Purple40)
            .width(150.dp)
            .height(200.dp)
        ) {

        }
        Box(
          Modifier
            .background(PurpleGrey80)
            .width(150.dp)
            .height(200.dp)
        ) {

        }
      }

      //player
      Row(
        Modifier
          .background(Color.Yellow)
          .padding(16.dp)
      ) {
        //failt Spot
        Box(
          Modifier
            .background(Purple40)
            .width(150.dp)
            .height(200.dp)
        ) {

        }

        Box(
          Modifier
            .background(PurpleGrey80)
            .width(150.dp)
            .height(200.dp)
        ) {

        }
        Box(
          Modifier
            .background(Purple40)
            .width(150.dp)
            .height(200.dp)
        ) {

        }
        Box(
          Modifier
            .background(PurpleGrey80)
            .width(150.dp)
            .height(200.dp)
        ) {

        }
      }

      // fack deck list
      val deck = mutableListOf<CardState>()

    for ( i in  1..20){
      deck.add(mockCardState.copy(false, mockCardState.cardData.copy(cost = i)))
    }

      val fakehand = deck.map { it.copy(true) }
      Row() {
        
        LazyRow(Modifier.weight(1f)) {
          items(items = fakehand) {
            DMGCard(it)
          }
        }
        Box() {
          deck.forEach {
            DMGCard(it)
          }
        }
      }
    }
  }
}

@Preview
@Composable
fun SideInfoBar() {
  
  Column(
    Modifier
      .background(Pink80)
      .fillMaxHeight()
      .fillMaxWidth(.10f),
  verticalArrangement = Arrangement.Center,
  horizontalAlignment = CenterHorizontally) {
    Text(text = "Life")
      //enamy life
    Text(text = "20 /20")
    Image(painter = painterResource(davidmedina.game.app.R.drawable.ic_baseline_battery_0_bar_24), contentDescription = "energy icon")
    Text(text = "Turn")
    Image(painter = painterResource(davidmedina.game.app.R.drawable.ic_baseline_battery_0_bar_24), contentDescription = "energy icon")
    Text(text = "Life")

   // user life
    Text(text = "20 /20")


  }
  
}