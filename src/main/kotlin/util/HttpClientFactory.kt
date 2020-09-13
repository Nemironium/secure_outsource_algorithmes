package util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.util.*
import kotlinx.serialization.json.Json

class HttpClientFactory(private val json: Json) {
    @KtorExperimentalAPI
    fun create(): HttpClient = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
        }

    }
}