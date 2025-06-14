package com.example.buyva.features.authentication.login.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.features.authentication.login.ui.LoginScreen
import com.example.buyva.features.authentication.login.viewmodel.LoginViewModel
import com.example.buyva.features.authentication.login.viewmodel.LoginViewModelFactory
import com.example.buyva.features.authentication.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreenHost(
    onSignUpClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val factory = remember {
        LoginViewModelFactory(AuthRepository(FirebaseAuth.getInstance()))
    }
    val viewModel: LoginViewModel = viewModel(factory = factory)

    LoginScreen(
        viewModel = viewModel,
        onSignUpClick = onSignUpClick,
        onLoginSuccess = onSuccess
    )
}
