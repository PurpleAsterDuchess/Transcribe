package com.example.transcribe.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface Repository <T>{
    suspend fun delete(transcription: T)
    suspend fun insert(transcription: T)
    suspend fun edit(transcription: T)
    suspend fun deleteAll()
    fun findAll(): Flow<List<T>>
    fun findById(id: Int): Flow<T?>
}

interface TranscriptionRepository : Repository<Transcription> {
    fun findByTitle(title: String): Flow<Transcription?>
    fun findByAuthor(author: String): Flow<Transcription?>

}

class LocalTranscriptionRepository @Inject constructor (private val transcriptionDAO: TranscriptionDAO):
    TranscriptionRepository {
        override suspend fun delete(transcription: Transcription) = transcriptionDAO.delete(transcription)
    override suspend fun deleteAll() = transcriptionDAO.deleteAll()
    override suspend fun edit(transcription: Transcription) = transcriptionDAO.update(transcription)
    override suspend fun insert(transcription: Transcription) = transcriptionDAO.insert(transcription)

    override fun findAll(): Flow<List<Transcription>> = transcriptionDAO.findAll()
    override fun findById(id: Int): Flow<Transcription?> = transcriptionDAO.findById(id)
    override fun findByTitle(title: String): Flow<Transcription?> =
        transcriptionDAO.findByTitle(title)
    override fun findByAuthor(author: String): Flow<Transcription?> =
        transcriptionDAO.findByAuthor(author)
}
