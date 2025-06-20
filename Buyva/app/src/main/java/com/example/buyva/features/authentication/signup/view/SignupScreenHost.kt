package com.example.buyva.features.authentication.signup.view

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buyva.R
import com.example.buyva.data.datasource.remote.graphql.ApolloService
import com.example.buyva.data.repository.AuthRepository
import com.example.buyva.features.authentication.signup.viewmodel.SignupViewModel
import com.example.buyva.features.authentication.signup.viewmodel.SignupViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.youssef.SignupScreen
@Composable
fun SignupScreenHost(
    onSignInClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    val factory = remember {
        val applicationContext = context.applicationContext
        SignupViewModelFactory(
            repository = AuthRepository(
                FirebaseAuth.getInstance(),
                ApolloService.client
            ),
            applicationContext = applicationContext
        )
    }

    val viewModel: SignupViewModel = viewModel(factory = factory)

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