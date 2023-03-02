package davidmedina.game.app.features.rpg.battle.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import davidmedina.game.app.features.rpg.battle.BattleCharacter
import davidmedina.game.app.features.rpg.battle.Battler
import davidmedina.game.app.features.rpg.data.ability.Ability
import davidmedina.game.app.ui.composables.gameBoxBackground


@Composable
fun BattleMenu(
    modifier: Modifier = Modifier,
    playerCharacters: List<BattleCharacter>,
    onCharacterSelected: (Battler) -> Unit,
    onAbility: (Ability) -> Unit,
    selectedCharacter: BattleCharacter?
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .gameBoxBackground()
    ) {
        AnimatedVisibility(selectedCharacter != null) {
            AbilitySelectMenu( selectedCharacter, onAbility)
        }
        Column(Modifier.weight(1f)) {
            playerCharacters.forEachIndexed { index, battleCharacter ->
                CharacterInfo(
                    characterStats = battleCharacter,
                    onCharacterSelected = { onCharacterSelected(Battler.Player(index = index)) })
            }

        }

    }
}

