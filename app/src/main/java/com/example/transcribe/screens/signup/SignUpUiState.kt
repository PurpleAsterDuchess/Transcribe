package com.example.transcribe.screens.signup

import android.util.Patterns

data class SignUpUiState(
    var firstName: String = "",
    var surname: String = "",
    var email: String = "",
    var password: String = ""
)
{
    fun firstNameIsInvalid(): Boolean{
        return firstName.trim().isEmpty()
    }
    fun surnameIsInvalid(): Boolean {
        return surname.trim().isEmpty()
    }
    fun emailIsInvalid(): Boolean {
        val trimmedEmail = email.trim()
        return trimmedEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()
    }

    fun passwordIsInvalid(): Boolean {
        return password.length < 6
    }

    fun isValid(): Boolean {
        return !firstNameIsInvalid() && !surnameIsInvalid() && !emailIsInvalid() && !passwordIsInvalid()
    }
}
