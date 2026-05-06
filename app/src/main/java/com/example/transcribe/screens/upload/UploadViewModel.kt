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
import com.example.transcribe.data.TranscriptionRepository
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repo: TranscriptionRepository,
    @ApplicationContext private val context: Context
): ViewModel() {
    var uiState by mutableStateOf(TranscriptionUIState())
        private set
        
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

    fun upload() {
        if (uiState.isValid()) {
            val currentUiState = uiState
            viewModelScope.launch(errorHandler) {
                var savedFileUri: String? = null
                
                currentUiState.selectedFileUri?.let { uri ->
                    savedFileUri = saveFileToInternalStorage(uri)
                }

                val newTranscription = Transcription(
                    title = currentUiState.title,
                    author = currentUiState.author,
                    fileUri = savedFileUri,
                )
                repo.insert(newTranscription)
                clear()
            }
        }
    }

    private suspend fun saveFileToInternalStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val fileName = "audio_${System.currentTimeMillis()}"
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

    val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("UploadViewModel", "Upload error: ${exception.message}")
    }

    private fun clear(){
        uiState = TranscriptionUIState()
    }
}
