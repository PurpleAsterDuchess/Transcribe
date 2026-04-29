package com.example.transcribe.screens.upload

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.transcribe.components.CustomTextField
import com.example.transcribe.components.CustomButton
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.example.transcribe.R


@Composable
fun UploadScreen(modifier: Modifier = Modifier,
                 vm: UploadViewModel = hiltViewModel(),
                 text: String,
                 context: Context,
                 onClickToHome: () -> Unit
){
    val keyboardController = LocalSoftwareKeyboardController.current

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        vm.onFileSelected(uri)
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                CustomTextField(
                    stringResource(R.string.title),
                    text = vm.uiState.title,
                    onValueChange = { vm.onChange(title = (it)) },
                    errorMessage = stringResource(R.string.title_error_msg),
                    errorPresent = vm.uiState.title.isNotBlank(),
                    modifier = Modifier.focusRequester(focusRequester)
                )
                CustomTextField(
                    stringResource(R.string.author),
                    text = vm.uiState.author,
                    onValueChange = { vm.onChange(author = (it)) },
                    errorMessage = stringResource(R.string.author_error_msg),
                    errorPresent = vm.uiState.author.isNotBlank(),
                    modifier = Modifier.focusRequester(focusRequester)
                )
                CustomButton(
                    text = "Select Binary File",
                    onClick = {
                        filePickerLauncher.launch("*/*")
                    }
                )
                CustomButton(
                    stringResource(R.string.upload_button),
                    onClick = {
                        vm.upload()
                        keyboardController?.hide()
                        onClickToHome()
                    })
            }
    }
    }
}