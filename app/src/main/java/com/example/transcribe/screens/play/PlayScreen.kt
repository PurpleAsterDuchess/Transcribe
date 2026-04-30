package com.example.transcribe.screens.play

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    songId: String?,
    navController: NavController,
    vm: PlayViewModel = hiltViewModel(),
    context: Context,
    modifier: Modifier = Modifier
) {
    val transcription = remember(songId) { vm.getTranscriptionById(songId) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
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

        if (vm.isPlayingSong) {
            androidx.compose.material3.Button(
                onClick = { vm.stopAudio() },
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Stop,
                        contentDescription = "Stop Icon"
                    )
                }
            }
        } else {
            androidx.compose.material3.Button(
                onClick = { vm.playAudio(context, transcription?.fileUri) },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow,
                        contentDescription = "Play Icon"
                    )
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(4.dp))
                }
            }
        }

    }
}