package com.example.buyva.features

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.R
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.Teal
import com.example.buyva.ui.theme.ubuntuItalic
import kotlinx.coroutines.launch

@Composable
fun SplashScreen() {
    val buyOffset = remember { Animatable(-300f) }
    val vaOffset = remember { Animatable(300f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            buyOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
        launch {
            vaOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, delayMillis = 1000)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(300.dp)
            )
            Row {
                Text(
                    text = "Buy",
                    fontSize = 48.sp,
                    fontFamily = ubuntuItalic,
                    color = Cold,
                    modifier = Modifier.offset(x = buyOffset.value.dp)
                )
                Text(
                    text = "Va",
                    fontSize = 48.sp,
                    fontFamily = ubuntuItalic,
                    color = Cold,
                    modifier = Modifier.offset(x = vaOffset.value.dp)
                )
            }
        }
    }
}
