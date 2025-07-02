package com.example.buyva.features.authentication.login.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.R
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.ui.theme.ubuntuMedium


@Composable
fun WelcomeScreen(
    onSignInClick: () -> Unit, onSignUpClick: () -> Unit, onSkipClick: () -> Unit
) {
    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF48A6A7), Color(0xFF006A71))))
            .padding(32.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = "Skip",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(onClick = onSkipClick)
                    .padding(end = 4.dp),
                fontFamily = ubuntuMedium
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .padding(bottom = 15.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Buyva",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = ubuntuMedium
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                "Your style, your store ",
                color = Color.White,
                fontSize = 28.sp,
                fontFamily = ubuntuMedium
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF48A6A7), Color(0xFF006A71))
                            ), shape = RoundedCornerShape(15.dp)
                        ), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SIGN IN",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        fontFamily = ubuntuMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onSignUpClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    "Create your account",
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = ubuntuMedium
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

