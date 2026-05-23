package com.example.transcribe.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    suspend fun insert(transcription: Transcription): String {
        val docRef = transcriptionCollection.add(transcription).await()
        return docRef.id
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
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Transcription::class.java)?.apply { id = doc.id }
                }
            }.catch { e ->
                emit(emptyList())
            }
    }

    fun getRecent(userId: String, limit: Long): Flow<List<Transcription>> {
        return transcriptionCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Transcription::class.java)?.apply { id = doc.id }
                }
            }.catch { e ->
                emit(emptyList())
            }
    }

    suspend fun getById(id: String): Transcription? {
        if (id.isEmpty()) return null
        val snapshot = transcriptionCollection.document(id).get().await()
        return snapshot.toObject(Transcription::class.java)?.apply { this.id = snapshot.id }
    }

    suspend fun getByTitle(title: String): Transcription? {
        val snapshot = transcriptionCollection.whereEqualTo("title", title).get().await()
        return snapshot.documents.firstOrNull()?.let { doc ->
            doc.toObject(Transcription::class.java)?.apply { id = doc.id }
        }
    }

    suspend fun getByAuthor(author: String): Transcription? {
        val snapshot = transcriptionCollection.whereEqualTo("author", author).get().await()
        return snapshot.documents.firstOrNull()?.let { doc ->
            doc.toObject(Transcription::class.java)?.apply { id = doc.id }
        }
    }
}
