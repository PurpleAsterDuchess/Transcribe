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
import com.example.transcribe.screens.PlayScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier
) {
    val context = LocalContext.current.applicationContext

    NavHost(
        navController = navController,
        startDestination = NavScreen.Home.route
    ) {

        composable(NavScreen.Home.route) {
            HomeScreen(
                navController = navController,
                text = stringResource(R.string.home_button),
                context = context,
                modifier = modifier
            )
        }

        composable(NavScreen.Upload.route) {
            UploadScreen(
                navController = navController,
                text = stringResource(R.string.upload_button),
                context = context,
                modifier = modifier
            )
        }

        composable(NavScreen.Favorites.route) {
            FavoritesScreen(
                navController = navController,
                text = stringResource(R.string.favorites_button),
                context = context,
                modifier = modifier
            )
        }

        composable(NavScreen.Play.route) {
            PlayScreen(
                navController = navController,
                text = stringResource(R.string.play_button),
                context = context,
                modifier = modifier
            )
        }
    }
}