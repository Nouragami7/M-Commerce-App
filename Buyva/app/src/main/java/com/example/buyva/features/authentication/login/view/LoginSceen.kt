package com.youssef

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buyva.R
@Composable
fun LoginScreen(
    onSignUpClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF48A6A7), Color(0xFF006A71))
                )
            )
    ) {
        // Header Text
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, start = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Hello", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Text("Sign in!", color = Color.White, fontSize = 35.sp)
        }

        // Card Content
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Email Field
                var email by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter your email", fontSize = 18.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF006A71),
                        unfocusedBorderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(20.dp))

                // Password Field with Eye Icon
                PasswordTextField()

                Spacer(Modifier.height(10.dp))

                // Forgot Password Text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Forgot password?",
                        color = Color(0xFF006A71),
                        fontSize = 15.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Sign In Button
                Button(
                    onClick = { /* Handle Sign-In */ },
                    modifier = Modifier
                        .width(280.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF48A6A7), Color(0xFF006A71))
                                ),
                                shape = RoundedCornerShape(30.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SIGN IN",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Continue with Google Button
                OutlinedButton(
                    onClick = onGoogleClick,
                    modifier = Modifier
                        .width(280.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continue with Google",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                Spacer(Modifier.height(100.dp))

                // Sign Up Text
                Row {
                    Text("Don't have an account?", color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign up",
                        color = Color(0xFF006A71),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onSignUpClick)
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordTextField() {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password", fontSize = 18.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF006A71),
            unfocusedBorderColor = Color.Gray
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSignInScreen() {
    LoginScreen()
}
