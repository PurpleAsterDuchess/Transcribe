package com.example.transcribe.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(private val auth: FirebaseAuth) : AuthRepo {
    override val currentUser get() = auth.currentUser

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
            // Send verification email immediately using the returned user object
            result.user?.sendEmailVerification()?.await()
            Response.NotConfirmed
        } catch (e: Exception) {
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
