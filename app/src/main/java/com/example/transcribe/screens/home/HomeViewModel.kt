package com.example.transcribe.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribe.data.LocalTranscriptionRepository
import com.example.transcribe.data.Transcription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: LocalTranscriptionRepository
) : ViewModel() {
    val items: StateFlow<List<Transcription>> = repo.findAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var selectedTranscription: Transcription?= null

    fun deleteTranscription(){
        val transcriptionToDelete = selectedTranscription ?: return
        viewModelScope.launch(errorHandler) {
            repo.delete(transcriptionToDelete)
            selectedTranscription = null
        }
    }

    val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("HomeViewModel", "Delete error: ${exception.message}")
    }
}
