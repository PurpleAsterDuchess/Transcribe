package com.example.transcribe.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.transcribe.R
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.transcribe.components.BottomNavBar
import com.example.transcribe.navigation.NavigationGraph
import androidx.navigation.compose.rememberNavController
import com.example.transcribe.components.DrawerContent
import com.example.transcribe.components.TopBar
import kotlinx.coroutines.launch
import com.example.transcribe.screens.play.PlayViewModel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.transcribe.navigation.NavScreen


@Composable
fun MainScreen(modifier: Modifier = Modifier,
               navController: NavHostController = rememberNavController(),
               vm: MainViewModel = hiltViewModel(),
               playVm: PlayViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentlySelectedMenuItem = navBackStackEntry?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentRoute = navBackStackEntry?.destination?.route

    val recentTranscriptions by vm.recentTranscriptions.collectAsStateWithLifecycle()

    var topBarTitle by remember { mutableStateOf("") }
    val defaultTitle = stringResource(R.string.app_name)

    val songId = navBackStackEntry?.arguments?.getString("songId")
    LaunchedEffect(songId) {
        if (songId != null) {
            playVm.getTranscriptionById(songId)
        }
    }

    val currentTranscription = playVm.currentTranscription

    val authRoutes = listOf(NavScreen.Login.route, NavScreen.SignUp.route)
    val showNav = currentRoute !in authRoutes

    LaunchedEffect(currentRoute, currentTranscription) {
        if (currentRoute?.contains("play") == true) {
            if (currentTranscription != null) {
                topBarTitle = "${currentTranscription?.title}, ${currentTranscription?.author}"
            } else {
                topBarTitle = "Unknown"
            }
        } else {
            topBarTitle = defaultTitle
        }
    }

    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (showNav) {
                ModalDrawerSheet {
                    DrawerContent(
                        menuTitle = stringResource(R.string.menu_name),
                        recentTranscriptions = recentTranscriptions,
                        selectedRoute = currentlySelectedMenuItem,
                        onItemClick = { menuItem ->
                            if (currentlySelectedMenuItem != menuItem.route) {
                                coroutineScope.launch {
                                    drawerState.close()
                                    navController.navigate(menuItem.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            } else {
                                coroutineScope.launch { drawerState.close() }
                            }
                        },
                        onTranscriptionClick = { transcription ->
                            coroutineScope.launch {
                                drawerState.close()
                                navController.navigate("${NavScreen.Play.route}/${transcription.id}")
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                if (showNav) {
                    TopBar(
                        text = topBarTitle,
                        onMenuIconClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        },
                        actions = {
                            if (currentRoute?.contains("play") == true && currentTranscription != null) {
                                IconButton(onClick = {
                                    if (playVm.isPlayingSong) {
                                        playVm.stopAudio()
                                    } else {
                                        playVm.playAudio(context, currentTranscription?.fileUri)
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (playVm.isPlayingSong) androidx.compose.material.icons.Icons.Default.Stop else androidx.compose.material.icons.Icons.Default.PlayArrow,
                                        contentDescription = if (playVm.isPlayingSong) "Stop" else "Play"
                                    )
                                }
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (showNav) {
                    BottomNavBar(
                        navController = navController
                    )
                }
            }
        ) { innerPadding ->
            NavigationGraph(navController = navController,
                modifier = modifier.padding(innerPadding))
        }
    }
}
