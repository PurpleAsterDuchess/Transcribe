package com.example.transcribe.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribe.R
import com.example.transcribe.UserRole
import com.example.transcribe.data.Response
import com.example.transcribe.components.CustomButton
import com.example.transcribe.components.ProgressBar
import com.example.transcribe.components.CustomTextField
import com.example.transcribe.screens.signup.SmallSpacer

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    vm: LoginViewModel = hiltViewModel(),
    navigateToSignUpScreen: () -> Unit,
    navigateToHomeScreen: (UserRole) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(vm.uiEvents) {
        vm.uiEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val response = vm.signInResponse

    LaunchedEffect(response) {
        if (response is Response.Success) {
            navigateToHomeScreen(vm.userRole)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { padding ->
            val keyboard = LocalSoftwareKeyboardController.current

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 32.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                CustomTextField(
                    hintText = stringResource(R.string.email),
                    text = vm.loginUiState.email,
                    onValueChange = { vm.onChange(email = it) },
                    errorMessage = stringResource(R.string.email_error_message),
                    errorPresent = vm.loginUiState.email.isNotEmpty() && vm.loginUiState.emailIsInvalid()
                )
                SmallSpacer()
                CustomTextField(
                    hintText = stringResource(R.string.password),
                    text = vm.loginUiState.password,
                    isPasswordField = true,
                    onValueChange = { vm.onChange(password = it) },
                    errorMessage = stringResource(R.string.password_error_message),
                    errorPresent = vm.loginUiState.password.isNotEmpty() && vm.loginUiState.passwordIsInvalid()
                )

                SmallSpacer()
                CustomButton(
                    stringResource(R.string.submit_button),
                    onClick = {
                        keyboard?.hide()
                        vm.signInWithEmailAndPassword()
                    },
                    enabled = vm.loginUiState.isValid() && response !is Response.Loading
                )

                SmallSpacer()
                CustomButton(
                    stringResource(R.string.forgot_password),
                    onClick = { vm.forgotPassword() },
                    enabled = vm.loginUiState.email.isNotEmpty() && !vm.loginUiState.emailIsInvalid() && response !is Response.Loading
                )

                SmallSpacer()
                CustomButton(stringResource(R.string.sign_up_button),
                    onClick = { navigateToSignUpScreen() },
                    enabled = response !is Response.Loading
                )
            }
        }
    )

    if (response is Response.Loading) {
        ProgressBar()
    }
}
