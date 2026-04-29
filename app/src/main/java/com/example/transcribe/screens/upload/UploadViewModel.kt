package com.example.transcribe.screens.upload

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.transcribe.screens.TranscriptionUIState
import java.util.UUID
import com.example.transcribe.data.Transcription
import com.example.transcribe.data.TranscriptionRepository
import androidx.compose.runtime.getValue

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repo: TranscriptionRepository<Transcription>
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

    fun upload(){
        if(uiState.isValid()) {
            val newTranscription = Transcription(
                id=UUID.randomUUID(),
                title=uiState.title,
                author=uiState.author,
                fileUri=uiState.selectedFileUri?.toString(),
            )
            repo.insert(newTranscription)

            Log.v("OK", "added, repo size is now ${repo.findAll().size}")
            clear()
        }
    }
    private fun clear(){
        uiState = TranscriptionUIState()
    }

}
