package com.example.transcribe.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.transcribe.navigation.NavScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination

@Composable
private fun createListOfItems(enabled: Boolean): List<NavScreen> {
    return listOf(
        NavScreen.Home,
        NavScreen.Upload,
        NavScreen.Favorites
    )
}

@Composable
fun BottomNavBar(navController: NavController) {

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        createListOfItems(enabled = true).forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.route } == true

            NavigationBarItem(
                selected = isSelected,
                label = {
                    Text(text = item.icon.label, fontSize = 9.sp)
                },
                icon = {
                    Icon(
                        imageVector = item.icon.icon,
                        contentDescription = item.icon.label
                    )
                },
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = false
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            )
        }
    }
}