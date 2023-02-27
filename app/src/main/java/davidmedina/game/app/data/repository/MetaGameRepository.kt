package davidmedina.game.app.data.repository

import davidmedina.game.app.Routes
import davidmedina.game.app.data.models.MetaGameState
import davidmedina.game.app.features.rpg.data.Character
import davidmedina.game.app.features.rpg.data.CharacterId
import davidmedina.game.app.features.rpg.data.Items
import davidmedina.game.app.features.rpg.data.createCharacter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.*


interface MetaGameRepository {


    fun getGameState(): Flow<MetaGameState>

    suspend fun logRoute(route: Routes)

    suspend fun register(userName: String)

    suspend fun updateParty(newParty: List<Character>)
}

class MetaGameRepositoryInMemory : MetaGameRepository {


    private val gameState = MutableStateFlow(
        MetaGameState(
            rpgCharacter = listOf(
                CharacterId.BLUE_OGER.createCharacter(),
                CharacterId.BLUE_OGER.createCharacter("number 2")
            ),
            rpgItems = listOf(Items.Potion(30))
        )
    )

    override fun getGameState(): Flow<MetaGameState> = gameState

    override suspend fun logRoute(route: Routes) {
        gameState.update { gameState ->
            val tempMap = gameState.screensVisited.toMutableMap()
            tempMap[route] = tempMap.getOrDefault(route, 0) + 1
            gameState.copy(screensVisited = tempMap)
        }
    }

    override suspend fun updateParty(newParty: List<Character>) {
        gameState.update {
            it.copy(rpgCharacter = newParty)
        }
    }

    override suspend fun register(userName: String) {
        gameState.update {
            it.copy(userName = userName, id = UUID.randomUUID())
        }
    }
}