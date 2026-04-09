package com.example.transcribe.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.transcribe.components.BottomNavBar
import com.example.transcribe.navigation.NavigationGraph
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(modifier: Modifier = Modifier,
               navController: NavHostController = rememberNavController()) {
    Scaffold(
        modifier = modifier,
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