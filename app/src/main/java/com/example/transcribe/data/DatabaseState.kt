package com.example.transcribe.data

sealed class DatabaseState<out T> {
    data object Loading : DatabaseState<Nothing>()
    data class Success<T>(val items: List<T>) : DatabaseState<T>()
    data class Failure(val message: String) : DatabaseState<Nothing>()
}