package davidmedina.game.app.ui.veiws

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import davidmedina.game.app.ui.theme.Pink80
import davidmedina.game.app.ui.theme.Purple80


sealed class CommunityFeedItem() {
  data class RewardUpdate( val info: String) : CommunityFeedItem()
  data class MerchUpdate(val details: String, @DrawableRes val drawable: Int) : CommunityFeedItem()
}



@Composable
fun CommunityFeedMerchItem(item: CommunityFeedItem.MerchUpdate) {
  Row(
    Modifier
      .padding(8.dp)
      .fillMaxWidth()
  ) {
//    Image(
//      painter = painterResource(R.),
//      contentDescription = "avatar",
//      contentScale = ContentScale.Crop,            // crop the image if it's not a square
//      modifier = Modifier
//        .size(64.dp)
//        .clip(CircleShape)
//
//      // clip to the circle shape
//    )
    Spacer(modifier = Modifier.padding(8.dp))
    
    Column() {
      Row(verticalAlignment = Alignment.CenterVertically) {
        //name
        Text(
          text = "SgtScribbles",
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold
        )
        //time
        Text(modifier = Modifier.padding(horizontal = 4.dp),
          text = "30m ago",
          fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.weight(1f))
        //button
        OutlinedButton(onClick = {} ,
          modifier =Modifier.height(20.dp),
          shape = MaterialTheme.shapes.large,
          border= BorderStroke(1.dp, Pink80),
          colors = ButtonDefaults.outlinedButtonColors(contentColor =  Pink80),
              contentPadding = PaddingValues(horizontal = 4.dp,)
        ) {
          Text(text = "Merch Update",
              fontSize = 10.sp,
            fontWeight = FontWeight.Light)
        }
      }


      //merch image
      Text(text = item.details,
        fontSize = 12.sp,
        modifier = Modifier.padding(vertical = 8.dp))
      Image(
        painter = painterResource(item.drawable),
        contentDescription = "avatar",
        contentScale = ContentScale.Crop,            // crop the image if it's not a square
        modifier = Modifier
          .fillMaxWidth(),


      )

      //cta button
      OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {} ,
        shape = MaterialTheme.shapes.large,
        border= BorderStroke(1.dp, Purple80),
        colors = ButtonDefaults.outlinedButtonColors(backgroundColor =  Purple80, contentColor = Color.White),

      ) {
        Text(
          text = "Redeem Now",
          fontWeight = FontWeight.Light
       )
      }

    }
  }
}

@Composable
fun CommunityFeedRewardUpdateItem(item: CommunityFeedItem.RewardUpdate) {
  Row(
    Modifier
      .padding(8.dp)
      .fillMaxWidth()
  ) {
//    Image(
//      painter = painterResource(R.drawable.image_5),
//      contentDescription = "avatar",
//      contentScale = ContentScale.Crop,            // crop the image if it's not a square
//      modifier = Modifier
//        .size(64.dp)
//        .clip(CircleShape)
//
//      // clip to the circle shape
//    )
    Spacer(modifier = Modifier.padding(8.dp))

    Column() {
      Row(verticalAlignment = Alignment.CenterVertically) {
        //name
        Text(
          text = "SgtScribbles",
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold
        )
        //time
        Text(modifier = Modifier.padding(horizontal = 4.dp),
          text = "30m ago",
          fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.weight(1f))
        //button
        OutlinedButton(onClick = {} ,
          modifier =Modifier.height(20.dp),
          shape = MaterialTheme.shapes.large,
          border= BorderStroke(1.dp, Purple80),
          colors = ButtonDefaults.outlinedButtonColors(contentColor =  Purple80),
          contentPadding = PaddingValues(horizontal = 4.dp,)
        ) {
          Text(text = "Reward Update",
            fontSize = 10.sp,
            fontWeight = FontWeight.Light)
        }
      }


      //merch image
      Text(text = item.info,
        fontSize = 12.sp,
        modifier = Modifier.padding(vertical = 8.dp))


      //cta button
      OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {} ,
        shape = MaterialTheme.shapes.large,
        border= BorderStroke(1.dp, Purple80),
        colors = ButtonDefaults.outlinedButtonColors(backgroundColor =  Purple80, contentColor = Color.White),

        ) {
        Text(
          text = "Join Points Bonanza",
          fontWeight = FontWeight.Light
        )
      }

    }
  }
}

