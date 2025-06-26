package com.example.buyva.features.authentication.login.view

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.buyva.R
import com.example.buyva.features.authentication.login.ui.LoginScreen
import com.example.buyva.features.authentication.login.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


@Composable
fun LoginScreenHost(
    onSignUpClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity




    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount = task.result
            viewModel.signInWithGoogle(account)
        } catch (e: Exception) {
            Log.e("GoogleLogin", "Google sign-in failed", e)
        }
    }

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    LoginScreen(
        viewModel = viewModel,
        onSignUpClick = onSignUpClick,
        onLoginSuccess = onSuccess,
        onGoogleClick = {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    )
}
