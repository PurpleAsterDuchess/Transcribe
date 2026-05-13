package com.example.transcribe.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

@Singleton
class TranscriptionDAO @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val transcriptionCollection = firestore.collection("transcriptions")

    suspend fun insert(transcription: Transcription) {
        transcriptionCollection.add(transcription).await()
    }

    suspend fun update(transcription: Transcription) {
        if (transcription.id.isNotEmpty()) {
            transcriptionCollection.document(transcription.id).set(transcription).await()
        }
    }

    suspend fun delete(transcriptionId: String) {
        if (transcriptionId.isNotEmpty()) {
            transcriptionCollection.document(transcriptionId).delete().await()
        }
    }

    fun getAll(): Flow<List<Transcription>> {
        return transcriptionCollection.orderBy("title")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Transcription::class.java)
            }.catch { e ->
                emit(emptyList())
            }
    }

    suspend fun getById(id: String): Transcription? {
        val snapshot = transcriptionCollection.document(id).get().await()
        return snapshot.toObject(Transcription::class.java)
    }

    suspend fun getByTitle(title: String): Transcription? {
        val snapshot = transcriptionCollection.whereEqualTo("title", title).get().await()
        return snapshot.toObjects(Transcription::class.java).firstOrNull()
    }

    suspend fun getByAuthor(author: String): Transcription? {
        val snapshot = transcriptionCollection.whereEqualTo("author", author).get().await()
        return snapshot.toObjects(Transcription::class.java).firstOrNull()
    }
}
