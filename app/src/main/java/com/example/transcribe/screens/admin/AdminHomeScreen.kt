package com.example.transcribe.screens.admin

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.transcribe.UserRole
import com.example.transcribe.data.Transcription

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    userRole: UserRole,
    navController: NavHostController,
    onIndexChange: (Transcription?) -> Unit,
    onClickToEdit: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    val selectedUserId by viewModel.selectedUserId.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()
    val userTranscriptions by viewModel.userTranscriptions.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedUserId == null) "User Management" else "User Details",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (selectedUserId != null) {
                        IconButton(onClick = { viewModel.selectUser(null) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedUserId == null) {
                if (users.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No users found.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(users) { user ->
                            UserItemCard(user = user, onClick = { viewModel.selectUser(user.uid) })
                        }
                    }
                }
            } else {
                selectedUser?.let { user ->
                    UserDetails(
                        user = user,
                        transcriptions = userTranscriptions,
                        onDeleteUser = {
                            viewModel.deleteUser(user.uid)
                            viewModel.selectUser(null)
                        },
                        onRoleChange = { newRole ->
                            viewModel.updateUserRole(user.uid, newRole)
                        },
                        onTranscriptionClick = { transcription ->
                            onIndexChange(transcription)
                            onClickToEdit()
                        }
                    )
                } ?: Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
