package com.example.transcribe.screens.upload

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.transcribe.screens.TranscriptionUIState
import com.example.transcribe.data.Transcription
import com.example.transcribe.data.Note
import com.example.transcribe.data.TranscriptionRepository
import com.example.transcribe.data.AuthRepo
import com.example.transcribe.data.UserRepo
import com.example.transcribe.LocalTranscriptionManager
import com.example.transcribe.TranscriptionApi
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repo: TranscriptionRepository,
    private val authRepo: AuthRepo,
    private val userRepo: UserRepo,
    private val localTranscriptionManager: LocalTranscriptionManager,
    private val api: TranscriptionApi,
    @ApplicationContext private val context: Context
): ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("UploadViewModel", "Upload error: ${exception.message}")
        uiState = uiState.copy(isUploading = false)
    }

    var uiState by mutableStateOf(TranscriptionUIState())
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch(errorHandler) {
            val uid = authRepo.getUserId()
            if (!uid.isNullOrEmpty()) {
                val user = userRepo.getById(uid)
                user?.let {
                    uiState = uiState.copy(author = "${it.firstName} ${it.surname}".trim())
                }
            }
        }
    }
        
    fun onFileSelected(uri: Uri?) {
        uiState = uiState.copy(selectedFileUri = uri)
    }

    fun onChange(
        title: String = uiState.title,
        author: String = uiState.author,
        selectedFileUri: Uri? = uiState.selectedFileUri
    ) {
        uiState = uiState.copy(
            title = title,
            author = author,
            selectedFileUri = selectedFileUri
        )
    }

    fun upload(onSuccess: () -> Unit = {}) {
        if (uiState.isValid() && uiState.selectedFileUri != null) {
            uiState = uiState.copy(isUploading = true)
            val currentUiState = uiState
            viewModelScope.launch(errorHandler) {
                val uid = authRepo.getUserId() ?: ""
                if (uid.isEmpty()) {
                    uiState = uiState.copy(isUploading = false)
                    return@launch
                }

                val user = userRepo.getById(uid)
                val authorName = user?.let { "${it.firstName} ${it.surname}".trim() } ?: currentUiState.author

                val savedFileUri = saveFileToInternalStorage(currentUiState.selectedFileUri!!)
                if (savedFileUri == null) {
                    uiState = uiState.copy(isUploading = false)
                    return@launch
                }

                val audioFile = File(Uri.parse(savedFileUri).path!!)
                val requestFile = audioFile.asRequestBody("audio/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)
                val jsonPart = "{}".toRequestBody("application/json".toMediaTypeOrNull())

                // Remote Transcription via API
                val response = try {
                    api.transcribeAudio(body, jsonPart)
                } catch (e: Exception) {
                    Log.e("UploadViewModel", "API Failed, falling back to local: ${e.message}")
                    localTranscriptionManager.transcribeLocal(Uri.parse(savedFileUri))
                }

                val newTranscription = Transcription(
                    title = currentUiState.title,
                    author = authorName,
                    fileUri = savedFileUri,
                    userId = uid,
                    sheetMusicUri = response.pdf_url,
                    midiUri = response.others_url,
                    voiceUri = response.voice_url,
                    scoreImageUrl = response.score_image_url,
                    notes = response.notes?.map { noteEvent ->
                        Note(
                            pitch = noteEvent.pitch,
                            start = noteEvent.start,
                            end = noteEvent.end,
                            velocity = noteEvent.velocity
                        )
                    } ?: emptyList()
                )

                // Save to Firestore/Local DB
                val generatedId = repo.insertTranscription(newTranscription)
                newTranscription.id = generatedId

                userRepo.addRecentTranscription(uid, newTranscription)
                
                clear()
                onSuccess()
            }
        }
    }

    private suspend fun saveFileToInternalStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val fileName = "audio_${System.currentTimeMillis()}.wav"
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { outputStream ->
                inputStream.use { input ->
                    input.copyTo(outputStream)
                }
            }
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            Log.e("UploadViewModel", "Error saving file: ${e.message}")
            null
        }
    }

    private fun clear(){
        uiState = TranscriptionUIState()
        loadUserProfile()
    }
}
