package com.example.buyva.utils.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.buyva.ui.theme.Cold

@Composable
fun LoadingIndicator(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center

    ){
        CircularProgressIndicator(
            color = Cold,
            strokeWidth = 4.dp
        )

    }


}