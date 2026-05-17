package com.example.transcribe

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    ADMIN("Admin", Icons.Default.AdminPanelSettings),
    FAVORITES("Favorites", Icons.Default.Favorite),
    UPLOAD("Upload", Icons.Default.Add),
    PLAY("Play", Icons.Default.PlayArrow),
    LOGIN("Login", Icons.Default.Login),
    SIGNUP("SignUp", Icons.Default.Add),
    EXIT("Exit", Icons.Default.ExitToApp)
}

enum class UserRole {
    USER,
    ADMIN,
    UNKNOWN
}
