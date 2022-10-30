package io.getspec.specmoble.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import davidmedina.game.app.ui.composables.PointsTextView


@Composable
fun RewardsItemView(rewardsItem: RewardsItem, imageSize: Dp = 80.dp) {
  Column {
    //image
    Image(

      painter = painterResource(rewardsItem.drawable),
      contentDescription = "avatar",
      modifier = Modifier
        .size(imageSize)
        .clip(RoundedCornerShape(20.dp))
        .background(Color.White),
      alignment = Alignment.Center
    )
    //text optional
    if (rewardsItem.details != null) {
      Text(
        modifier = Modifier.size(imageSize),
        text = rewardsItem.details,
        fontSize = 12.sp,
        fontWeight = FontWeight.Light
      )
    }
    // price
    PointsTextView(fontSize = 18.sp, pointValue = "500")

  }

}

data class RewardsItem(@DrawableRes val drawable: Int, val details: String? = null)