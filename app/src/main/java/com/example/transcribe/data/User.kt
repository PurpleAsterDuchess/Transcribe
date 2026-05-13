package com.example.transcribe.data

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val uid: String = "",
    val firstName: String = "",
    val surname: String = "",
    val email: String = "",
    val role: UserRole = UserRole.ADMIN
)