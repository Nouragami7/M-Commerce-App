package com.example.buyva.features.authentication.signup.view

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.buyva.features.authentication.signup.viewmodel.SignupViewModel
import com.youssef.SignupScreen

@Composable
fun SignupScreenHost(
    onSignInClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel(),
    navController: NavController

) {
    SignupScreen(
        viewModel = viewModel,
        onSignInClick = onSignInClick,
        onSuccess = onSuccess,
        navController =navController
    )
}