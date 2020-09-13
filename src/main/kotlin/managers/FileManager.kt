package managers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import entities.OperationResult
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.charset.Charset

class FileManager(private val json: Json) {
    suspend fun <T>processReadFile(path: String, serializer: KSerializer<T>): OperationResult<T> {
        return withContext(Dispatchers.IO) {
            return@withContext when(val jsonData = readFileToString(path)) {
                is OperationResult.Success -> decodeJson(jsonData.data, serializer)
                is OperationResult.Error -> OperationResult.Error(jsonData.error)
            }
        }
    }

    private fun readFileToString(path: String): OperationResult<String> {
        return try {
            val jsonData: String
            FileInputStream(path).use {
                jsonData = it.readBytes().toString(Charset.defaultCharset())
            }
            OperationResult.Success(jsonData)
        } catch (e: FileNotFoundException) {
            OperationResult.Error("Cannot find file $path")
        } catch (e: SecurityException) {
            OperationResult.Error("Cannot read file $path")
        }
    }

    private fun <T>decodeJson(jsonData: String, serializer: KSerializer<T>): OperationResult<T> {
        return try {
            OperationResult.Success(json.decodeFromString(serializer, jsonData))
        } catch (e: SerializationException) {
            OperationResult.Error("Cannot deserialize data")
        }
    }
}