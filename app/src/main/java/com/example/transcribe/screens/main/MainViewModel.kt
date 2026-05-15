package com.example.transcribe.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribe.data.AuthRepo
import com.example.transcribe.data.Transcription
import com.example.transcribe.data.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val authRepo: AuthRepo
) : ViewModel() {
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val recentTranscriptions: StateFlow<List<Transcription>> = authRepo.authStateFlow
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(emptyList())
            } else {
                userRepo.getUserFlow(user.uid)
                    .map { it?.recentTranscriptions ?: emptyList() }
                    .map { list -> list.filter { it.id.isNotEmpty() && it.title.isNotEmpty() } }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
