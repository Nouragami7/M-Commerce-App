package com.example.buyva.features.authentication.signup.view

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.buyva.R
import com.example.buyva.features.authentication.signup.viewmodel.SignupViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.youssef.SignupScreen

@Composable
fun SignupScreenHost(
    onSignInClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()

) {
    val context = LocalContext.current
    val activity = context as Activity



    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account: GoogleSignInAccount = task.result
            viewModel.signUpWithGoogle(account)
        } catch (e: Exception) {
            Log.e("GoogleSignup", "Google sign-up failed", e)
        }
    }

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    SignupScreen(
        viewModel = viewModel,
        onSignInClick = onSignInClick,
        onSuccess = onSuccess,
        onGoogleClick = {
            googleSignInClient.signOut().addOnCompleteListener {
                val intent = googleSignInClient.signInIntent
                launcher.launch(intent)
            }
        }
    )
}