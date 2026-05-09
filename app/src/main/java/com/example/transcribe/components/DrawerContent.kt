package com.example.transcribe.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transcribe.AppDestinations
import com.example.transcribe.navigation.NavScreen

// This should be popluated with links to the play screens, each of which is a different score
@Composable
fun DrawerContent(
    menuTitle: String,
    selectedRoute: String?,
    onItemClick: (NavScreen) -> Unit
) {
    val tempDrawerItems = listOf(
        NavScreen.Play,
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    modifier = Modifier.padding(vertical = 24.dp),
                    text = menuTitle,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            items(
                items = tempDrawerItems,
                key = { it.route }
            ) { item ->
                NavigationDrawerItem(
                    label = {
                        Text(text = item.route.replaceFirstChar { it.uppercase() })
                    },
                    selected = item.route == selectedRoute,
                    onClick = { onItemClick(item) },
                    icon = {
                        Icon(
                            imageVector = item.icon.icon,
                            contentDescription = item.route,
                        )
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        NavigationDrawerItem(
            label = {
                Text(text = "Sign Out")
            },
            selected = false,
            onClick = { onItemClick(NavScreen.EXIT) },
            icon = {
                Icon(
                    imageVector = NavScreen.EXIT.icon.icon,
                    contentDescription = "Sign Out",
                )
            },
            modifier = Modifier
                .padding(NavigationDrawerItemDefaults.ItemPadding)
                .padding(bottom = 12.dp)
        )
    }
}
