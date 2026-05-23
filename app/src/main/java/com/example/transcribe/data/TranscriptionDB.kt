package com.example.transcribe.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Transcription::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TranscriptionDB : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: TranscriptionDB? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE Transcriptions ADD COLUMN sheetMusicUri TEXT")
                    db.execSQL("ALTER TABLE Transcriptions ADD COLUMN midiUri TEXT")
                    db.execSQL("ALTER TABLE Transcriptions ADD COLUMN voiceUri TEXT")
                    db.execSQL("ALTER TABLE Transcriptions ADD COLUMN scoreImageUrl TEXT")
                    db.execSQL("ALTER TABLE Transcriptions ADD COLUMN notes TEXT")
                } catch (e: Exception) {
                }
            }
        }

        fun getDatabase(context: Context): TranscriptionDB {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TranscriptionDB::class.java,
                    "transcription_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
