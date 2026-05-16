package com.example.transcribe.data

import kotlinx.coroutines.flow.Flow

interface Repository <T>{
    suspend fun delete(id: String)
    suspend fun insert(transcription: T)
    suspend fun edit(transcription: T)
    fun getAll(): Flow<List<T>>
    suspend fun getById(id: String): T?
}