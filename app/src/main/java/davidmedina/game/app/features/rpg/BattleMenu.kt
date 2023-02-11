package davidmedina.game.app.features.rpg

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import davidmedina.game.app.ui.composables.gameBoxBackground


@Composable
fun BattleMenu(
    modifier: Modifier = Modifier,
    onAction: () -> Unit = {},
) {
    Row(
        modifier = modifier.then(Modifier)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 200.dp)
            .gameBoxBackground()

    ) {}
}





