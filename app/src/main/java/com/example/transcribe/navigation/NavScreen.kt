package com.example.transcribe.navigation

import com.example.transcribe.AppDestinations

sealed class NavScreen(
    val icon: AppDestinations,
    val route: String
) {
    data object Home : NavScreen(AppDestinations.HOME, "home")
    data object Admin_Home : NavScreen(AppDestinations.ADMIN, "admin_home")
    data object Upload : NavScreen(AppDestinations.UPLOAD, "upload")
    data object Favorites : NavScreen(AppDestinations.FAVORITES, "favorites")
    data object Play : NavScreen(AppDestinations.PLAY, "play")
    data object Login : NavScreen(AppDestinations.LOGIN, "login")
    data object SignUp : NavScreen(AppDestinations.SIGNUP, "signup")
    data object EXIT : NavScreen(AppDestinations.EXIT, "exit")
}
