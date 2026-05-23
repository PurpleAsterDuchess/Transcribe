package com.example.transcribe.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.transcribe.UserRole
import com.example.transcribe.data.Transcription
import com.example.transcribe.data.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetails(
    user: User,
    transcriptions: List<Transcription>,
    onDeleteUser: () -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onTranscriptionClick: (Transcription) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDeleteUser) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete User",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Name: ${user.firstName} ${user.surname}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "UID: ${user.uid}", style = MaterialTheme.typography.bodySmall)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Role:", style = MaterialTheme.typography.labelLarge)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = user.role.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        UserRole.entries.filter { it != UserRole.UNKNOWN }.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.name) },
                                onClick = {
                                    onRoleChange(role)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Transcriptions (${transcriptions.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (transcriptions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No Transcriptions found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transcriptions) { transcription ->
                    AdminTranscriptionCard(
                        transcription = transcription,
                        onClick = { onTranscriptionClick(transcription) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminTranscriptionCard(transcription: Transcription, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = transcription.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Author: ${transcription.author}",
                style = MaterialTheme.typography.bodySmall
            )
            transcription.createdAt?.let {
                Text(
                    text = "Created: $it",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
