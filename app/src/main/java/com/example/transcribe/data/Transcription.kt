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
    var createdAt: Date? = null
) {
    @PrimaryKey
    var id: String = ""

    override fun toString(): String = "$title, $author"
}
