package com.example.transcribe.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextField(hintText: String,
                    text: String,
                    onValueChange: (String) -> Unit,
                    errorMessage: String,
                    errorPresent: Boolean,
                    modifier: Modifier = Modifier){

    Surface(modifier = Modifier.padding(10.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            isError = errorPresent,
            singleLine = true,
            label = {
                Text(hintText)
            },
            modifier = modifier
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text =  if (errorPresent) "" else errorMessage,
            fontSize = 14.sp,
            color = Color.Red,
        )
    }
}
