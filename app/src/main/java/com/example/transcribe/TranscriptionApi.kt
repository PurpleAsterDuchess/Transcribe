package com.example.transcribe

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TranscriptionApi {
    @Multipart
    @POST("api")
    suspend fun transcribeAudio(
        @Part file: MultipartBody.Part,
        @Part("json") payload: RequestBody
    ): TranscriptionResponse
}

data class TranscriptionResponse(
    val success: Boolean,
    val pdf_url: String?,
    val voice_url: String?,
    val others_url: String?,
    val score_image_url: String?,
    val notes: List<NoteEvent>?,
    val msg: String?
)

data class NoteEvent(
    val pitch: Int,
    val start: Float,
    val end: Float,
    val duration: Float,
    val velocity: Int
)
