package com.youssef

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.buyva.R
import com.example.buyva.features.authentication.signup.viewmodel.SignupViewModel

@Composable

fun SignupScreen(
    viewModel: SignupViewModel,
    onSignInClick: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val user by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()

    // ✅ لما التسجيل ينجح نروح للشاشة الرئيسية
    LaunchedEffect(user) {
        if (user != null) {
            onSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF48A6A7), Color(0xFF006A71))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, start = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Welcome", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Text("Create your Account", color = Color.White, fontSize = 35.sp)
        }

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
                    .padding(horizontal = 24.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                var fullName by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }
                var passwordVisible by remember { mutableStateOf(false) }
                var confirmPasswordVisible by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name", fontSize = 18.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF006A71),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", fontSize = 18.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF006A71),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(Modifier.height(20.dp))

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
                        val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF006A71),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password", fontSize = 18.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF006A71),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        viewModel.signUp(fullName, email, password, confirmPassword)
                    }
                    ,
                    modifier = Modifier
                        .width(280.dp)
                        .height(50.dp),
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
                            text = "SIGN UP",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(5.dp))

                OutlinedButton(
                    onClick = { /* Handle Google Sign-Up */ },
                    modifier = Modifier
                        .width(280.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(30.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Sign up with Google", color = Color.Black, fontSize = 16.sp)
                }

                Spacer(Modifier.height(30.dp))

                error?.let {
                    Text(text = it, color = Color.Red)
                }

                Spacer(Modifier.height(20.dp))

                Row {
                    Text("Already have an account?", color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign in",
                        color = Color(0xFF006A71),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onSignInClick)
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordTextField(label: String) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text(label, fontSize = 18.sp) },
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
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF006A71),
            unfocusedBorderColor = Color.Gray
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    SignupScreen(
        viewModel = TODO(),
        onSignInClick = TODO(),
        onSuccess = TODO()
    )
}
