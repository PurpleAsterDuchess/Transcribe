package com.example.transcribe.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.transcribe.screens.HomeScreen
import com.example.transcribe.screens.UploadScreen
import com.example.transcribe.screens.FavoritesScreen
import com.example.transcribe.R

@Composable
fun NavigationGraph(navController: NavHostController,
                    modifier: Modifier){
    val context = LocalContext.current.applicationContext
    NavHost(navController,
        startDestination = NavScreen.Home.route) {
        composable(NavScreen.Home.route) {
            HomeScreen(text = stringResource(R.string.home_button),
                context = context,
                modifier)
        }
        composable(NavScreen.Upload.route) {
            UploadScreen(text = stringResource(R.string.upload_button),
                context = context,
                modifier)
        }
        composable(NavScreen.Upload.route) {
            FavoritesScreen(text = stringResource(R.string.favorites_button),
                context = context,
                modifier)
        }
    }
}