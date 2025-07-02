package com.youssef

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.buyva.R
import com.example.buyva.features.authentication.signup.viewmodel.SignupViewModel
import com.example.buyva.navigation.ScreensRoute
import com.example.buyva.navigation.navbar.NavigationBar

@Composable
fun SignupScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    onSignInClick: () -> Unit = {},
    onSuccess: () -> Unit = {},
    navController: NavController
) {
    //Hide nav bar
    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = false
    }
    val UbuntuFontFamily = FontFamily(
        Font(R.font.ubuntu_medium,FontWeight.Medium)
    )
  //  val user by viewModel.user.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val isEmailVerified by viewModel.isEmailVerified.collectAsStateWithLifecycle()

    LaunchedEffect(isEmailVerified) {
            Log.d("SignupScreen", "User is not null")
            if (isEmailVerified) {
                onSuccess()
                Log.d("SignupScreen", "User is verified")
                navController.navigate(ScreensRoute.LoginScreen)
                Log.d("SignupScreen", "Navigating to LoginScreen")
            } else{
            Log.d("SignupScreen", "User is null")
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
            Text("Welcome", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold, fontFamily = UbuntuFontFamily)
            Text("Create your Account", color = Color.White, fontSize = 35.sp,fontFamily = UbuntuFontFamily)
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
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color(0xFF006A71),
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color(0xFF006A71),
                        focusedLabelColor = Color(0xFF006A71),
                        unfocusedLabelColor =Color.Gray,

                    )
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", fontSize = 18.sp) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color(0xFF006A71),
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color(0xFF006A71),
                        focusedLabelColor = Color(0xFF006A71),
                        unfocusedLabelColor =Color.Gray,
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
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color(0xFF006A71),
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color(0xFF006A71),
                        focusedLabelColor = Color(0xFF006A71),
                        unfocusedLabelColor =Color.Gray,

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
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color(0xFF006A71),
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color(0xFF006A71),
                        focusedLabelColor = Color(0xFF006A71),
                        unfocusedLabelColor =Color.Gray,

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
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                Spacer(Modifier.height(30.dp))

                error?.let {
                    Text(text = it, color = Color.Red)
                }

                Spacer(Modifier.height(20.dp))

                Row {
                    Text("Already have an account?", color = Color.Gray,fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign in",
                        color = Color(0xFF006A71),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onSignInClick),
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
