package com.example.transcribe.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transcription::class],
    version = 1,
    exportSchema = false)
abstract class TranscriptionDB : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: TranscriptionDB? = null

        fun getDatabase(context: Context) : TranscriptionDB {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context,
                    TranscriptionDB::class.java,
                    "contact_database")
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}