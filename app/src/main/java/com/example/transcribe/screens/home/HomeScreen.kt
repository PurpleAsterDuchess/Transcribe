package com.example.transcribe.screens.home

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.transcribe.R
import com.example.transcribe.navigation.NavScreen
import com.example.transcribe.components.CustomButton
import com.example.transcribe.data.Transcription
import com.example.transcribe.data.DatabaseState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    vm: HomeViewModel = hiltViewModel(),
    onIndexChange: (Transcription?) -> Unit,
    onClickToEdit: () -> Unit,
    navController: NavController,
    context: Context,
) {
    val NOTHING_SELECTED = -1
    val snackbarHostState = remember { SnackbarHostState() }

    val transcriptionState by vm.transcriptions.collectAsStateWithLifecycle()
    var selectedIndexToHighlight by remember { mutableIntStateOf(NOTHING_SELECTED) }


    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.home_button),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.size(30.dp))

            if (transcriptionState is DatabaseState.Success) {
                val transcriptionList = (transcriptionState as DatabaseState.Success<Transcription>).items

                if (transcriptionList.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No transcriptions found")
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        itemsIndexed(transcriptionList) { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ItemView(
                                    index = index,
                                    item = item.toString(),
                                    selected = selectedIndexToHighlight == index,
                                    onClick = { clickedIndex ->
                                        vm.selectedTranscription = item
                                        onIndexChange(item)
                                        selectedIndexToHighlight = clickedIndex
                                    }
                                )
                            }
                        }
                    }
                }

                val isItemSelected = selectedIndexToHighlight != NOTHING_SELECTED
                        && transcriptionList.isNotEmpty()

                Spacer(modifier = Modifier.size(30.dp))

                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    CustomButton(
                        text = "Play",
                        onClick = {
                            val id = transcriptionList[selectedIndexToHighlight].id
                            navController.navigate("${NavScreen.Play.route}/$id")
                        },
                        enabled = isItemSelected
                    )

                    Spacer(modifier = Modifier.size(16.dp))
                }
            }

            if (transcriptionState is DatabaseState.Loading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            if (transcriptionState is DatabaseState.Failure) {
                val errorMessage = (transcriptionState as DatabaseState.Failure).message
                LaunchedEffect(errorMessage) {
                    snackbarHostState.showSnackbar(errorMessage)
                }
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Error loading database", color = Color.Red)
                }
            }
        }
    }
}