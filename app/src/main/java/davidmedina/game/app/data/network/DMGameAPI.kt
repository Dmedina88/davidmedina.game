package davidmedina.game.app.data.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

import kotlinx.coroutines.delay

val client = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

interface DMGameApi {
    suspend fun login(userName: String, password: String)
}

class DMGameApiImpl : DMGameApi {
    override suspend fun login(userName: String, password: String) {
        delay(1000L)
    }

}

