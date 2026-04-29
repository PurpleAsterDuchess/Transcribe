package com.example.transcribe.data
import java.util.UUID

data class Transcription(
    var id: UUID?,
    var title: String,
    var author: String,
    val fileUri: String? = null
) {
    override fun toString(): String = "$title, $author"
}
