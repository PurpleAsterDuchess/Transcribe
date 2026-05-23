package com.example.transcribe.data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import java.util.Date

@Entity(tableName = "Transcriptions")
data class Transcription(
    var title: String = "",
    var author: String = "",
    var fileUri: String? = null,
    var userId: String = "",
    var createdAt: Date? = null,
    var sheetMusicUri: String? = null,
    var midiUri: String? = null,
    var voiceUri: String? = null,
    var scoreImageUrl: String? = null,
    var isFavorite: Boolean = false,
    var notes: List<Note> = emptyList()
) {
    @PrimaryKey
    var id: String = ""

    override fun toString(): String = "$title, $author"
}

data class Note(
    var pitch: Int = 0,
    var start: Float = 0f,
    var end: Float = 0f,
    var velocity: Int = 0
)
