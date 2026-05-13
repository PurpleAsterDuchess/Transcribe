package com.example.transcribe.data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId

@Entity(tableName = "Transcriptions")
data class Transcription(
    var title: String = "",
    var author: String = "",
    var fileUri: String? = null

) {
    @PrimaryKey
    @DocumentId
    var id: String = ""

    override fun toString(): String = "$title, $author"
}
