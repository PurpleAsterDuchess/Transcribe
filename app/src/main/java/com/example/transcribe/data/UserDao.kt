package com.example.transcribe.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserDao @Inject constructor(private val firestore: FirebaseFirestore
) {
    private val userCollection = firestore.collection("users")

    suspend fun create(user: User): Response{
        return try {
            update(user)
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    suspend fun add(user: User) {
        userCollection.document(user.uid).set(user).await()
    }

    suspend fun update(user: User) {
        userCollection.document(user.uid).set(user).await()
    }

    suspend fun delete(userId: String) {
        userCollection.document(userId).delete().await()
    }

    fun getAll(): Flow<List<User>> {
        return userCollection.orderBy("email")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(User::class.java)?.apply { uid = doc.id }
                    } catch (e: Exception) {
                        Log.e("UserDao", "Skipping malformed user doc: ${doc.id}")
                        null
                    }
                }
            }.catch { e ->
                emit(emptyList())
            }
    }

    suspend fun getById(id: String): User? {
        return try {
            if (id.isBlank()) return null
            val snapshot = userCollection.document(id).get().await()
            snapshot.toObject(User::class.java)?.apply { uid = snapshot.id }
        } catch (e: Exception) {
            Log.e("UserDao", "Error fetching user $id: ${e.message}")
            null
        }
    }

    fun getUserFlow(uid: String): Flow<User?> {
        return userCollection.document(uid)
            .snapshots()
            .map { snapshot ->
                try {
                    snapshot.toObject(User::class.java)?.apply { this.uid = snapshot.id }
                } catch (e: Exception) {
                    Log.e("UserDao", "Error in user flow for $uid: ${e.message}")
                    null
                }
            }.catch { e ->
                emit(null)
            }
    }

    suspend fun addRecentTranscription(uid: String, transcription: Transcription) {
        try {
            if (uid.isBlank() || transcription.id.isBlank()) return
            
            val userDoc = userCollection.document(uid)
            val snapshot = userDoc.get().await()
            val user = snapshot.toObject(User::class.java)?.apply { this.uid = snapshot.id } ?: return
            
            val currentRecents = user.recentTranscriptions.filter {
                it.id.isNotEmpty() && it.title.isNotEmpty() 
            }.toMutableList()
            
            if (transcription.createdAt == null) {
                transcription.createdAt = java.util.Date()
            }
            
            currentRecents.add(0, transcription)
            val updatedRecents = currentRecents.distinctBy { it.id }.take(5)
            
            userDoc.update("recentTranscriptions", updatedRecents).await()
        } catch (e: Exception) {
            Log.e("UserDao", "Failed to add recent transcription: ${e.message}")
        }
    }

    suspend fun removeRecentTranscription(uid: String, transcriptionId: String) {
        try {
            if (uid.isBlank() || transcriptionId.isBlank()) return
            
            val userDoc = userCollection.document(uid)
            val snapshot = userDoc.get().await()
            val user = snapshot.toObject(User::class.java)?.apply { this.uid = snapshot.id } ?: return
            
            val updatedRecents = user.recentTranscriptions.filter { it.id != transcriptionId }
            
            userDoc.update("recentTranscriptions", updatedRecents).await()
        } catch (e: Exception) {
            Log.e("UserDao", "Failed to remove recent transcription: ${e.message}")
        }
    }
}
