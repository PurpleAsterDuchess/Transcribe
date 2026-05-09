package com.example.transcribe.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

interface AuthRepo {
    val currentUser: FirebaseUser?
    suspend fun signInWithEmailAndPassword(email: String, password: String): Response
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Response
    suspend fun sendEmailVerification(): Response
    suspend fun sendPasswordResetEmail(email: String): Response
    fun signOut()
}

class AuthRepository @Inject constructor(private val auth: FirebaseAuth) : AuthRepo {
    override val currentUser get() = auth.currentUser

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Response {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): Response {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun sendEmailVerification(): Response {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }


    override suspend fun sendPasswordResetEmail(email: String): Response {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Response.Success
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override fun signOut() = auth.signOut()
}