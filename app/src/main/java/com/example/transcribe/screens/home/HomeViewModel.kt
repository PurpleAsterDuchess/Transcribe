package com.example.transcribe.screens.home

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.transcribe.data.Transcription
import com.example.transcribe.data.TranscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: TranscriptionRepository<Transcription>
) : ViewModel() {
    val items = mutableStateListOf<Transcription>()

    init{
        items.addAll(repo.findAll())
    }

    fun deleteTranscription(index: Int){
        val selectedTranscription = repo.findById(index)
        repo.delete(selectedTranscription)
        items.remove(selectedTranscription)
        Log.v("OK", "deleted, repo size is now ${repo.findAll().size}")
    }
}
