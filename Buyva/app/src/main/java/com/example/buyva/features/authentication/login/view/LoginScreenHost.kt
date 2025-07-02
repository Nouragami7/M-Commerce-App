package com.example.buyva.features.authentication.login.view

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.buyva.features.authentication.login.ui.LoginScreen
import com.example.buyva.features.authentication.login.viewmodel.LoginViewModel


@Composable
fun LoginScreenHost(
    onSignUpClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {

    LoginScreen(
        viewModel = viewModel,
        onSignUpClick = onSignUpClick,
        onLoginSuccess = onSuccess,
    )
}
