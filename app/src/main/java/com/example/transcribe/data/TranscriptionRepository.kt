package com.example.transcribe.data

import dagger.Provides
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface Repository <T>{
    suspend fun delete(id: String)
    suspend fun insert(transcription: T)
    suspend fun edit(transcription: T)
    fun getAll(): Flow<List<T>>
    suspend fun getById(id: String): T?
}

interface TranscriptionRepository : Repository<Transcription> {
    suspend fun getByTitle(title: String): Transcription?
    suspend fun getByAuthor(author: String): Transcription?

}

class LocalTranscriptionRepository @Inject constructor (
    private val transcriptionDAO: TranscriptionDAO
) : TranscriptionRepository {
    override suspend fun delete(id: String) {
        transcriptionDAO.delete(id)
    }
    override suspend fun edit(transcription: Transcription) {
        transcriptionDAO.update(transcription)
    }
    override suspend fun insert(transcription: Transcription) {
        if (transcription.id.isEmpty()) {
            transcriptionDAO.insert(transcription)
        }
    }
    override fun getAll(): Flow<List<Transcription>> = transcriptionDAO.getAll()
    override suspend fun getById(id: String): Transcription? = transcriptionDAO.getById(id)
    override suspend fun getByTitle(title: String): Transcription? =
        transcriptionDAO.getByTitle(title)
    override suspend fun getByAuthor(author: String): Transcription? =
        transcriptionDAO.getByAuthor(author)
}
