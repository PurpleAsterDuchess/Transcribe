package com.example.transcribe.screens.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribe.data.AuthRepository
import com.example.transcribe.data.Response
import com.example.transcribe.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import com.example.transcribe.data.UserRepo


@HiltViewModel
class SignUpViewModel @Inject constructor (
private val auth: AuthRepository,
private val userRepo: UserRepo
) : ViewModel() {
    var signUpUiState by mutableStateOf(SignUpUiState())
        private set
    fun onChange(firstName: String = signUpUiState.firstName,
                 surname: String = signUpUiState.surname,
                 email: String = signUpUiState.email,
                 password: String = signUpUiState.password) {

        signUpUiState = signUpUiState.copy(firstName = firstName,
            surname = surname,
            email = email,
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
            if (signUpUiState.isValid()) {
                signUpResponse = Response.Loading
                signUpResponse = auth.signUpWithEmailAndPassword(signUpUiState.email, signUpUiState.password)

                if (signUpResponse is Response.Failure) {
                    _uiEvents.send("Unable to create sign up")
                }
                if (signUpResponse is Response.NotConfirmed){
                    sendEmailVerification()
                    saveUserDetailsToFirestore()
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
            if (sendEmailVerificationResponse is Response.Success){
                _uiEvents.send("Confirm details via email")
            }
        }
    }

    fun saveUserDetailsToFirestore() {
        val uid = auth.getUserId()?: return

        val newUserDetails = User(
            uid = uid,
            firstName = signUpUiState.firstName,
            surname = signUpUiState.surname,
            email = signUpUiState.email,
        )

        viewModelScope.launch {
            val response = userRepo.createUserProfile(newUserDetails)
            if (response is Response.Failure) {
                _uiEvents.send("Profile sync failed: ${response.e.message}")
            }
        }
    }
}