package com.example.transcribe.screens.home

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.transcribe.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribe.navigation.NavScreen
import com.example.transcribe.components.CustomButton


@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               vm: HomeViewModel = hiltViewModel(),
               selectedIndex: Int,
               onIndexChange: (Int) -> Unit,
               navController: NavController,
               text: String,
               context: Context,
) {
    val items by vm.items.collectAsState()
    val NOTHING_SELECTED = -1

    Scaffold(modifier = modifier) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding).fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            CustomButton(
                text = "Play this song",
                onClick = {
                    if (selectedIndex != NOTHING_SELECTED && selectedIndex < items.size) {
                        val selectedId = items[selectedIndex].id
                        navController.navigate("${NavScreen.Play.route}/$selectedId")
                    }
                },
                enabled = selectedIndex != NOTHING_SELECTED && items.isNotEmpty()
            )

            LazyColumn (modifier = Modifier.weight(1f)) {
                itemsIndexed(items) { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ItemView(
                            index = index,
                            item = item.toString(),
                            selected = selectedIndex == index,
                            onClick = onIndexChange
                        )
                    }
                }
            }
        }
    }

}
