package pl.tinks.budgetbuddy

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val e: Throwable) : Result<Nothing>()
}
