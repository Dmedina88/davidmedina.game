package davidmedina.game.app.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import davidmedina.game.app.data.models.backgrounds
import davidmedina.game.app.ui.theme.GradientColors


@Composable
fun TDMAGradientButton(
    gradient : GradientColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    content: @Composable () -> Unit,
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        onClick = { onClick() },
    ) {
        Box(
            modifier = Modifier
                .background(brush = Brush.verticalGradient(gradient.colors))
                .defaultMinSize(ButtonDefaults.MinWidth, ButtonDefaults.MinHeight)
                .padding(ButtonDefaults.ContentPadding)
                .then(modifier),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}
//preview of the gradient button
@Preview
@Composable
fun GradientButtonPreview() {
    Row(modifier = Modifier.padding(43.dp)) {
        TDMAGradientButton(
            gradient = GradientColors.PurpleGradient,
            onClick = {}
        ) {
            Text("Gradientel;fker;ljgeorkgoekropwger Button")
        }
        ElevatedButton(onClick = { /*TODO*/ }) {
            Text("Defult Button")

        }
    }
}