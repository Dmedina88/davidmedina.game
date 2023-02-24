package davidmedina.game.app.data.repository

import davidmedina.game.app.data.network.DMGameApi

interface LoginRepository {

    suspend fun login(userName: String, password: String)
}

class LoginRepositoryImpl(val dmgApi: DMGameApi) : LoginRepository {
    override suspend fun login(userName: String, password: String) {
        dmgApi.login(userName, password)
    }

}

