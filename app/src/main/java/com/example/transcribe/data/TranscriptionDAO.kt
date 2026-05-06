package com.example.transcribe.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TranscriptionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transcription: Transcription)

    @Update
    suspend fun update(transcription: Transcription)

    @Delete
    suspend fun delete(transcription: Transcription)

    @Query("DELETE FROM Transcriptions")
    suspend fun deleteAll()

    @Query("SELECT * FROM Transcriptions ORDER BY author, title ASC")
    fun findAll(): Flow<List<Transcription>>

    @Query("SELECT * FROM Transcriptions WHERE id =:id")
    fun findById(id: Int): Flow<Transcription?>

    @Query("SELECT * FROM Transcriptions WHERE author =:author ORDER BY author, title ASC")
    fun findByAuthor(author: String): Flow<Transcription?>

    @Query("SELECT * FROM Transcriptions WHERE title =:title ORDER BY title, author ASC")
    fun findByTitle(title: String): Flow<Transcription?>
}