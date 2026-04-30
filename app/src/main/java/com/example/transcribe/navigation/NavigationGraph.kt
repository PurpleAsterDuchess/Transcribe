package com.example.transcribe.navigation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.transcribe.screens.FavoritesScreen
import com.example.transcribe.R
import com.example.transcribe.screens.play.PlayScreen
import com.example.transcribe.screens.home.HomeScreen
import com.example.transcribe.screens.upload.UploadScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier
) {
    val context = LocalContext.current.applicationContext
    var selectedContactIndex by remember{ mutableIntStateOf(-1) }

    NavHost(
        navController = navController,
        startDestination = NavScreen.Home.route
    ) {

        composable(NavScreen.Home.route) {
            HomeScreen(
                selectedIndex = selectedContactIndex,
                onIndexChange = {
                    selectedContactIndex = it
                },
                navController = navController,
                text = stringResource(R.string.home_button),
                context = context,
                modifier = modifier
            )
        }

        composable(NavScreen.Upload.route) {
            UploadScreen(
                text = stringResource(R.string.upload_button),
                context = context,
                modifier = modifier,
                onClickToHome = { navController.navigate("home") }
            )
        }

        composable(NavScreen.Favorites.route) {
            FavoritesScreen(
                navController = navController,
                text = stringResource(R.string.favorites_button),
                context = context,
                modifier = modifier,
//                selectedContactIndex = selectedContactIndex,
//                onClickToHome = {
//                    if (selectedContactIndex != -1)
//                        navController.popBackStack()
//                }
            )
        }

        composable(
            route = "${NavScreen.Play.route}/{songId}"
        ) {backStackEntry ->    val songId = backStackEntry.arguments?.getString("songId")
            PlayScreen(
                navController = navController,
                songId= songId,
                context = context,
                modifier = modifier
            )
        }

//        composable(NavScreen.EXIT.route){
//            exitProcess(0);
//        }
    }
}