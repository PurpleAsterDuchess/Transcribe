package com.example.transcribe.data

import com.google.firebase.auth.FirebaseUser

interface AuthRepo {
    val currentUser: FirebaseUser?
    suspend fun signInWithEmailAndPassword(email: String, password: String): Response
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Response
    fun getUserId(): String?
    suspend fun sendEmailVerification(): Response
    suspend fun sendPasswordResetEmail(email: String): Response
    fun signOut()
}
