package com.example.transcribe.screens.main

import androidx.lifecycle.ViewModel
import com.example.transcribe.data.Transcription
import com.example.transcribe.data.TranscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    val repo: TranscriptionRepository<Transcription>
) : ViewModel()
