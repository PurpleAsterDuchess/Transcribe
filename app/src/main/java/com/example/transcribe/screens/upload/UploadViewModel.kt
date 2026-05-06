package com.example.transcribe.screens.upload

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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repo: TranscriptionRepository
): ViewModel() {
    var uiState by mutableStateOf(TranscriptionUIState())
        private set
    fun onFileSelected(uri: android.net.Uri?) {
        uiState = uiState.copy(selectedFileUri = uri)
    }

    fun onChange(
        title: String = uiState.title,
        author: String = uiState.author,
        selectedFileUri: android.net.Uri? = uiState.selectedFileUri
    ) {
        uiState = uiState.copy(
            title = title,
            author = author,
            selectedFileUri = selectedFileUri
        )
    }

    fun upload() {
        if (uiState.isValid()) {
            val newTranscription = Transcription(
                title = uiState.title,
                author = uiState.author,
                fileUri = uiState.selectedFileUri?.toString(),
            )
            viewModelScope.launch(errorHandler) {
                repo.insert(newTranscription)
                clear()
            }
        }
    }

    val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("UploadViewModel", "Upload error: ${exception.message}")
    }

    private fun clear(){
        uiState = TranscriptionUIState()
    }

}
