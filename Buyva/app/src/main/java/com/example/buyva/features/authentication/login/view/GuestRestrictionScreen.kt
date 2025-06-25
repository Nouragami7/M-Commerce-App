package com.example.buyva.features.authentication.login.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.ui.theme.Sea


@Composable
fun GuestRestrictionScreen(
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = "Restricted",
            modifier = Modifier.size(120.dp),
            tint = Sea
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Sign In Required",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This feature is only available for registered users. Sign in or create an account to continue.",
            color = Color.Black,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(containerColor = Sea)

            ) {
                Text("Sign In")
            }

            // Sign Up Button
            OutlinedButton(
                onClick = onSignUp,
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(containerColor = Sea)

            ) {
                Text("Create Account")
            }

            // Continue as Guest Button
            TextButton(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = Sea)

            ) {
                Text("Continue Browsing")
            }
        }
    }
}

