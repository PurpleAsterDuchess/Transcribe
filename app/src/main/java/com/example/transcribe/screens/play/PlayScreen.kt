package com.example.transcribe.screens.play

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.transcribe.components.CustomButton
import com.example.transcribe.components.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    songId: String?,
    navController: NavController,
    vm: PlayViewModel = hiltViewModel(),
    context: Context,
    modifier: Modifier = Modifier
) {
    val transcription by vm.getTranscriptionById(songId)
        .collectAsStateWithLifecycle(initialValue = null)

    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf("") }
    var editedAuthor by remember { mutableStateOf("") }

    LaunchedEffect(transcription) {
        transcription?.let {
            if (!isEditing) {
                editedTitle = it.title
                editedAuthor = it.author
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isEditing) {
            CustomTextField(
                hintText = "Title",
                text = editedTitle,
                onValueChange = { editedTitle = it },
                errorMessage = "Title cannot be empty",
                errorPresent = editedTitle.isEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
            CustomTextField(
                hintText = "Author",
                text = editedAuthor,
                onValueChange = { editedAuthor = it },
                errorMessage = "Author cannot be empty",
                errorPresent = editedAuthor.isEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                CustomButton(
                    text = "Save",
                    onClick = {
                        transcription?.let {
                            it.title = editedTitle
                            it.author = editedAuthor
                            vm.updateTranscription(it)
                            isEditing = false
                        }
                    },
                    enabled = editedTitle.isNotEmpty() && editedAuthor.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                CustomButton(
                    text = "Cancel",
                    onClick = { isEditing = false },
                    containerColor = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Box(modifier = modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = transcription?.title ?: "Unknown Title",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 20.dp)
                    )


                    Text(
                        text = "Author: ${transcription?.author ?: "Unknown"}",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    }

//            CustomButton(
//                onClick = {
//                    if (vm.isPlayingSong) {
//                        vm.stopAudio()
//                    } else {
//                        vm.playAudio(context, transcription?.fileUri)
//                    }
//                },
//                text = if (vm.isPlayingSong) "Stop Audio" else "Play Audio",
//                modifier = Modifier.fillMaxWidth(0.7f),
//                containerColor = if (vm.isPlayingSong) Color.Red else Color.Black
//            )
            Row (modifier = Modifier.align(Alignment.TopEnd)){
                IconButton(onClick = { isEditing = true}) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                        contentDescription = "edit"
                    )
                }
                IconButton(
                    onClick = {
                        transcription?.let {
                            vm.deleteTranscription(it) {
                                navController.popBackStack()
                            }
                        }
                    },
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                        contentDescription = "edit"
                    )
                }
            }
            }
        }
    }
}
