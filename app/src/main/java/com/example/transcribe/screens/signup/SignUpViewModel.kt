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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@HiltViewModel
class SignUpViewModel @Inject constructor (private val auth: AuthRepository
) : ViewModel() {
    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun onChange(email: String = loginUiState.email,
                 password: String = loginUiState.password) {
        loginUiState = loginUiState.copy(email = email,
            password = password)
    }

    private val _uiEvents = Channel<String>()
    val uiEvents = _uiEvents.receiveAsFlow()

    var signUpResponse by mutableStateOf<Response>(Response.Startup)
        private set

    var sendEmailVerificationResponse by mutableStateOf<Response>(Response.Startup)
        private set

    fun signUpWithEmailAndPassword() {
        viewModelScope.launch {
            if (loginUiState.isValid()) {
                signUpResponse = Response.Loading
                signUpResponse = auth.signUpWithEmailAndPassword(loginUiState.email, loginUiState.password)

                if (signUpResponse is Response.Failure) {
                    _uiEvents.send("Unable to create")
                }
                if (signUpResponse is Response.NotConfirmed){
                    sendEmailVerification()
                }
            }
        }
    }

    fun sendEmailVerification() {
        viewModelScope.launch {
            sendEmailVerificationResponse = Response.Loading
            sendEmailVerificationResponse = auth.sendEmailVerification()

            if (sendEmailVerificationResponse is Response.Failure) {
                _uiEvents.send("Unable to send verification email")
            }
            else{
            _uiEvents.send("Confirm details via email")
            }
        }
    }
}