package com.example.transcribe.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.transcribe.R
import com.example.transcribe.navigation.NavScreen


@Composable
fun HomeScreen(navController: NavController,
               text: String,
               context: Context,
               modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Button(
            onClick = {
                navController.navigate(NavScreen.Play.route)
            }
        ) {
            Text("Play this song")
        }
    }
}