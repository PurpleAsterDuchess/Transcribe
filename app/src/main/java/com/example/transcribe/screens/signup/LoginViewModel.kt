package com.example.transcribe.screens.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribe.data.AuthRepository
import com.example.transcribe.data.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(private val auth: AuthRepository) : ViewModel() {
    var loginUiState by mutableStateOf(LoginUiState())

    fun onChange(email: String = loginUiState.email, password: String = loginUiState.password) {
        loginUiState = loginUiState.copy(email = email, password = password)
    }

    private val _uiEvents = Channel<String>()
    val uiEvents = _uiEvents.receiveAsFlow()

    val isEmailVerified get() = auth.currentUser?.isEmailVerified ?: false

    var signInResponse by mutableStateOf<Response>(Response.Startup)
        private set

    fun forgotPassword() {
        viewModelScope.launch {
            val message = when (auth.sendPasswordResetEmail(loginUiState.email)) {
                is Response.Success -> "Password reset email has been sent successfully"
                is Response.Failure -> "Unable to send reset email"
                else -> "An unexpected error occurred"
            }
            _uiEvents.send(message)
        }
    }

    fun signInWithEmailAndPassword() {
        viewModelScope.launch {
            signInResponse = Response.Loading
            signInResponse = auth.signInWithEmailAndPassword(loginUiState.email, loginUiState.password)

            if (signInResponse is Response.Failure) {
                _uiEvents.send("Unable to sign in: ${(signInResponse as Response.Failure).e.message}")
            } else if (!isEmailVerified) {
                auth.sendEmailVerification()
                _uiEvents.send("Email not verified. A new verification link has been sent.")
            }
        }
    }
}