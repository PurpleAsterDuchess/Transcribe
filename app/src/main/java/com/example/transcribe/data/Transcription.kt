package com.example.transcribe.data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Transcriptions")
data class Transcription(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "author") var author: String,
    @ColumnInfo(name = "fileUri") var fileUri: String? = null

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int? = null

    override fun toString(): String = "$title, $author"
}
