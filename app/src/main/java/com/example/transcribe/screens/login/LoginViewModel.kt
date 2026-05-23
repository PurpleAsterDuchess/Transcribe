package com.example.transcribe.screens.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribe.UserRole
import com.example.transcribe.data.AuthRepo
import com.example.transcribe.data.Response
import com.example.transcribe.data.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: AuthRepo,
    private val userRepo: UserRepo
) : ViewModel() {
    var loginUiState by mutableStateOf(LoginUiState())
    var userRole by mutableStateOf(UserRole.UNKNOWN)
        private set

    fun onChange(email: String = loginUiState.email,
                 password: String = loginUiState.password) {
        loginUiState = loginUiState.copy(email = email,
            password = password)
    }

    private val _uiEvents = Channel<String>()
    val uiEvents = _uiEvents.receiveAsFlow()

    val isEmailVerified get() = auth.currentUser?.isEmailVerified ?: false

    var signInResponse by mutableStateOf<Response>(Response.Startup)
        private set

    fun forgotPassword() {
        viewModelScope.launch {
            signInResponse = Response.Loading
            val message = try {
                withTimeout(10000L) {
                    val result = auth.sendPasswordResetEmail(loginUiState.email)

                    when (result) {
                        is Response.Success -> "Password reset email has been sent successfully"
                        is Response.Failure -> "Unable to send reset email: ${result.e.message}"
                        else -> "An unexpected error occurred"
                    }
                }
            } catch (e: TimeoutCancellationException) {
                "Request timed out. Please check your internet connection."
            } catch (e: Exception) {
                e.message ?: "An unexpected error occurred"
            }
            signInResponse = Response.Startup
            _uiEvents.send(message)
        }
    }

    fun signInWithEmailAndPassword() {
        viewModelScope.launch {
            signInResponse = Response.Loading
            val response = auth.signInWithEmailAndPassword(loginUiState.email, loginUiState.password)

            if (response is Response.Success) {
                try {
                    val userId = auth.getUserId() ?: throw Exception("User ID not found after sign-in")
                    userRole = userRepo.getUserRole(userId)
                    
                    if (isEmailVerified) {
                        signInResponse = Response.Success
                    } else {
                        signInResponse = Response.NotConfirmed
                        _uiEvents.send("Email not verified")
                    }
                } catch (e: Exception) {
                    Log.e("LoginViewModel", "Error fetching user role", e)
                    signInResponse = Response.Failure(e)
                }
            } else if (response is Response.Failure) {
                signInResponse = response
            }

            if (signInResponse is Response.Failure) {
                _uiEvents.send("Unable to sign in: ${(signInResponse as Response.Failure).e.message}")
            }
        }
    }
}
