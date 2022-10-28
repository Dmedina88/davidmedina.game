package davidmedina.game.app.ui.veiws

import android.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PointsTextView(modifier: Modifier = Modifier,fontSize: TextUnit = 36.sp, pointValue: String  = "1,245") {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.End,
    verticalAlignment = Alignment.CenterVertically,

  ) {
    Text(
      fontWeight = FontWeight.SemiBold,
      fontSize = fontSize,
      text =  pointValue,
      modifier = Modifier.padding(horizontal = 5.dp)
    )



  }
}