package com.example.transcribe.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
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
import com.example.transcribe.data.Transcription
import com.example.transcribe.data.TranscriptionRepository


@Composable
fun MainScreen(modifier: Modifier = Modifier,
               navController: NavHostController = rememberNavController(),
               vm: MainViewModel = hiltViewModel()
){

    val coroutineScope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentlySelectedMenuItem = navBackStackEntry?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentRoute = navBackStackEntry?.destination?.route

    var topBarTitle by remember { mutableStateOf("") }
    val defaultTitle = stringResource(R.string.app_name)
    LaunchedEffect(currentRoute, navBackStackEntry) {
        if (currentRoute?.contains("play") == true) {
            val songId = navBackStackEntry?.arguments?.getString("songId")
            if (songId != null) {
                val song = vm.repo.findAll().find { it.id.toString() == songId }
                topBarTitle = "${song?.title}, ${song?.author}"
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
            ModalDrawerSheet {
                DrawerContent(
                    menuTitle = stringResource(R.string.menu_name),
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
                        }
                        else { coroutineScope.launch { drawerState.close() }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopBar(
                    text = topBarTitle,
                    onMenuIconClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBar(
                    navController = navController
                )
            }
        ) { innerPadding ->
            NavigationGraph(navController = navController,
                modifier = modifier.padding(innerPadding))
        }
    }
}