package com.example.transcribe.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribe.data.DatabaseState
import com.example.transcribe.data.LocalTranscriptionRepository
import com.example.transcribe.data.Transcription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: LocalTranscriptionRepository
) : ViewModel() {
    val transcriptions: StateFlow<DatabaseState<Transcription>> = repo.getAll()
        .map<List<Transcription>, DatabaseState<Transcription>> { list ->
            DatabaseState.Success(list)
        }
        .catch { e ->
            emit(DatabaseState.Failure(e.message ?: "Unknown Error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DatabaseState.Loading
        )
    var selectedTranscription: Transcription?= null

    fun deleteTranscription(){
        val transcriptionToDelete = selectedTranscription?.id ?: return
        viewModelScope.launch(errorHandler) {
            repo.delete(transcriptionToDelete)
            selectedTranscription = null
        }
    }

    val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("HomeViewModel", "Delete error: ${exception.message}")
    }
}
