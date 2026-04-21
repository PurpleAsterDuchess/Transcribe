package com.example.transcribe.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.transcribe.R
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.transcribe.components.BottomNavBar
import com.example.transcribe.navigation.NavigationGraph
import androidx.navigation.compose.rememberNavController
import com.example.transcribe.components.DrawerContent
import com.example.transcribe.components.TopBar
import kotlinx.coroutines.launch



@Composable
fun MainScreen(modifier: Modifier = Modifier,
               navController: NavHostController = rememberNavController()) {

    val coroutineScope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentlySelectedMenuItem = navBackStackEntry?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

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
                    text = stringResource (R.string.app_name),
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