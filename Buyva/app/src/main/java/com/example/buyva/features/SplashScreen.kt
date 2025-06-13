package com.example.buyva.features

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.R
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.ubuntuItalic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SplashScreen(onCompletion: () -> Unit) {
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
            targetValue = 1f, animationSpec = tween(durationMillis = 1000, delayMillis = 1000)
        )
    }

    LaunchedEffect(Unit) {
        delay(3000)
        onCompletion()
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray), contentAlignment = Alignment.Center
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
                Text(text = "Buy",
                    fontSize = 48.sp,
                    fontFamily = ubuntuItalic,
                    color = Cold,
                    modifier = Modifier.offset {
                        IntOffset(buyOffset.value.roundToInt(), 0)
                    })
                Text(text = "Va",
                    fontSize = 48.sp,
                    fontFamily = ubuntuItalic,
                    color = Cold,
                    modifier = Modifier.offset {
                        IntOffset(vaOffset.value.roundToInt(), 0)
                    })
            }
        }
    }
}
