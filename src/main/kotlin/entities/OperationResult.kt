package entities

sealed class OperationResult<out T> {
    data class Success<out T>(val data: T): OperationResult<T>()
    data class Error<out T>(val error: String): OperationResult<T>()
}