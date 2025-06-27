package com.example.buyva.features.profile.profileoptions.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.buyva.features.authentication.login.viewmodel.UserSessionManager
import com.example.buyva.features.authentication.signup.viewmodel.LogoutViewModel
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.navigation.navbar.NavigationBar.resetToDefaultTab
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.Sea
import com.example.buyva.ui.theme.Teal
import com.example.buyva.utils.components.ScreenTitle
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    onFavClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    logoutViewModel: LogoutViewModel = hiltViewModel()
) {
    val isGuest = UserSessionManager.isGuest()

    LaunchedEffect(Unit) {
        NavigationBar.mutableNavBarState.value = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Gray)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle("Profile")

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Teal),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isGuest) "Hello, Guest" else "Hello, ${FirebaseAuth.getInstance().currentUser?.displayName}",
            fontStyle = FontStyle.Italic,
            fontSize = 18.sp,
            color = Cold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            if (!isGuest) {
                ProfileOption(Icons.Default.LocalShipping, "My Orders", Sea, onOrdersClick)
                ProfileOption(Icons.Default.FavoriteBorder, "My Wishlist", Sea, onFavClick)
                ProfileOption(Icons.Default.LocationOn, "Delivery Address", Sea, onAddressClick)
            }

            ProfileOption(Icons.Default.Money, "Currency", Sea, onCurrencyClick)
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedButton(
            onClick = {
                if (isGuest) {
                    onLoggedOut()
                    resetToDefaultTab()
                } else {
                    logoutViewModel.logout()
                    onLoggedOut()
                    resetToDefaultTab()
                }
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Sea),
            border = BorderStroke(1.dp, Sea)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = if (isGuest) "Register" else "Logout")
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isGuest) "Log In" else "Log Out",
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}


@Composable
fun ProfileOption(icon: ImageVector, title: String, iconColor: Color, onAddressClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
          .fillMaxWidth()
            .padding(vertical = 9.dp)
            .height(60.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onAddressClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

