package davidmedina.game.app.data.network

import kotlinx.coroutines.delay


interface DMGameApi {
    suspend fun login(userName:String, password : String )
}

class DMGameApiImpl() : DMGameApi{
    override suspend fun login(userName: String, password: String) {
        delay(1000L)
    }

}

