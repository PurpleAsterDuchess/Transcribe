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
import com.example.transcribe.components.CustomTextField
import com.example.transcribe.components.CustomButton
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.transcribe.R
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator


@Composable
fun UploadScreen(modifier: Modifier = Modifier,
                 vm: UploadViewModel = hiltViewModel(),
                 text: String,
                 context: Context,
                 onClickToHome: () -> Unit
){
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState = vm.uiState

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        vm.onFileSelected(uri)
    }

    val fileName = vm.uiState.selectedFileUri?.lastPathSegment ?: "Select Binary File"

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
                    text = uiState.title,
                    onValueChange = { vm.onChange(title = it) },
                    errorMessage = stringResource(R.string.title_error_msg),
                    errorPresent = uiState.title.isBlank() && uiState.title.isNotEmpty(),
                    modifier = Modifier.focusRequester(focusRequester)
                )
                CustomTextField(
                    stringResource(R.string.author),
                    text = uiState.author,
                    onValueChange = { vm.onChange(author = (it)) },
                    errorMessage = stringResource(R.string.author_error_msg),
                    errorPresent = uiState.author.isBlank() && uiState.author.isNotEmpty(),
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.2f))
                        .border(
                            1.dp, Color.Gray,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = !uiState.isUploading) { filePickerLauncher.launch("audio/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (uiState.selectedFileUri == null) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = "Upload Icon",
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Text(
                            text = if (uiState.selectedFileUri != null)
                                "Selected: $fileName" else "Tap to select a file",
                            fontSize = 14.sp,
                            color = if (uiState.selectedFileUri != null)
                                Color.Black else Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                CustomButton(
                    stringResource(R.string.upload_button),
                    onClick = {
                        keyboardController?.hide()
                        vm.upload(onSuccess = onClickToHome)
                    },
                    enabled = uiState.isValid() && uiState.selectedFileUri != null && !uiState.isUploading
                )
            }
        }

        if (uiState.isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Transcribing with Basic Pitch...", color = Color.White)
                }
            }
        }
    }
}
