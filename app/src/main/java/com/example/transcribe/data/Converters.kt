package com.example.transcribe.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromNoteList(value: List<Note>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toNoteList(value: String?): List<Note>? {
        val listType = object : TypeToken<List<Note>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
