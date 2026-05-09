package com.example.transcribe.navigation

import androidx.lifecycle.ViewModel
import com.example.transcribe.data.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val auth: AuthRepo
) : ViewModel() {
    fun signOut() {
        auth.signOut()
    }
}