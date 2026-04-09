package com.example.transcribe.navigation

import com.example.transcribe.AppDestinations

sealed class NavScreen(var icon: AppDestinations,
    var route:String){
    data object Home: NavScreen(AppDestinations.HOME, "Home")
    data object Upload: NavScreen(AppDestinations.UPLOAD, "Upload")
    data object Favorites: NavScreen(AppDestinations.FAVORITES, "Favorites")
}