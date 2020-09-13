package util

import kotlinx.serialization.json.Json

class JsonFactory {
    fun create(): Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }
}