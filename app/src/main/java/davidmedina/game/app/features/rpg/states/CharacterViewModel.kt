package davidmedina.game.app.features.rpg.states

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import davidmedina.game.app.data.repository.MetaGameRepository
import davidmedina.game.app.features.rpg.battle.Action
import davidmedina.game.app.features.rpg.battle.BattleCharacter
import davidmedina.game.app.features.rpg.battle.BattleStage
import davidmedina.game.app.features.rpg.battle.Battler
import davidmedina.game.app.features.rpg.data.Character
import davidmedina.game.app.features.rpg.data.Items
import davidmedina.game.app.features.rpg.data.ability.abilityList
import davidmedina.game.app.util.TickHandler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class CharacterViewModel(private val metaGameRepository: MetaGameRepository) : ViewModel() {

    var playerCharacters = mutableStateListOf<Character>()
        private set


     init {
        viewModelScope.launch {
            metaGameRepository.getGameState().collect {
                playerCharacters.clear()
                playerCharacters.addAll(it.rpgCharacter)
            }

        }
    }
}
