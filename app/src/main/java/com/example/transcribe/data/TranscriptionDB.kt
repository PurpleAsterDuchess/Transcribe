package com.example.transcribe.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transcription::class],
    version = 1,
    exportSchema = false)
@TypeConverters(Converters::class)
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