package davidmedina.game.app.features.rpg.battle.ui


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import davidmedina.game.app.features.rpg.battle.BattleStateMachine
import davidmedina.game.app.features.rpg.data.battleImage


@Composable
fun ActionChip(battleStateMachine: BattleStateMachine) {
    Row(modifier = Modifier.padding(34.dp)) {
        AnimatedVisibility(visible = battleStateMachine.currentPlayerAction?.attacker != null) {
            val character =
                battleStateMachine.playerCharacters[battleStateMachine.currentPlayerAction?.attacker?.index
                    ?: 0]
            Image(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = character.characterStats.characterID.battleImage),
                contentDescription = ""
            )
        }

        AnimatedVisibility(visible = battleStateMachine.currentPlayerAction?.ability != null) {
            Text(
                color = Color.Yellow,
                text = battleStateMachine.currentPlayerAction?.ability?.name.toString()
            )
        }
    }
}

