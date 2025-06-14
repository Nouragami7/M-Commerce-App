package com.example.buyva.features.authentication.signup.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.features.authentication.repository.AuthRepository
import com.example.buyva.features.authentication.signup.viewmodel.SignupViewModel
import com.example.buyva.features.authentication.signup.viewmodel.SignupViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.youssef.SignupScreen
@Composable
fun SignupScreenHost(
    onSignInClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val factory = remember {
        SignupViewModelFactory(AuthRepository(FirebaseAuth.getInstance()))
    }
    val viewModel: SignupViewModel = viewModel(factory = factory)

    SignupScreen(
        viewModel = viewModel,
        onSignInClick = onSignInClick,
        onSuccess = onSuccess
    )
}
