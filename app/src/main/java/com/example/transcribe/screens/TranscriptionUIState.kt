package com.example.transcribe.screens

data class TranscriptionUIState(
    val title: String = "",
    val author: String = "",
    val selectedFileUri: android.net.Uri? = null,
    val isUploading: Boolean = false
) {
    override fun toString(): String = "$title, $author"

    fun isValid(): Boolean {
        return title.isNotBlank() && author.isNotBlank()
    }
}