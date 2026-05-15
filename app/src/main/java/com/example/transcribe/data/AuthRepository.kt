package com.example.transcribe.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(private val auth: FirebaseAuth) : AuthRepo {
    override val currentUser get() = auth.currentUser

    override val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Response {
        return try {
            auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): Response {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()

            result.user?.sendEmailVerification()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthRepository", "Verification email sent successfully.")
                } else {
                    Log.e("AuthRepository", "Failed to send verification email: ${task.exception?.message}")
                }
            }

            Response.NotConfirmed
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign-up failed: ${e.message}")
            Response.Failure(e)
        }
    }

    override fun getUserId(): String?{
        return auth.currentUser?.uid
    }
    
    override suspend fun sendEmailVerification(): Response {
        val user = auth.currentUser
        return if (user != null) {
            try {
                user.sendEmailVerification().await()
                Response.Success
            } catch (e: Exception) {
                Response.Failure(e)
            }
        } else {
            Response.Failure(Exception("User not authenticated. Please log in again."))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Response {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override fun signOut() = auth.signOut()
}
