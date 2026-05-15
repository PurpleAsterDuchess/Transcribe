package com.example.transcribe.data

import com.google.firebase.firestore.DocumentId

data class User(
    var uid: String = "",
    var firstName: String = "",
    var surname: String = "",
    var email: String = "",
    var role: UserRole = UserRole.ADMIN,
    var recentTranscriptions: List<Transcription> = emptyList()
)
