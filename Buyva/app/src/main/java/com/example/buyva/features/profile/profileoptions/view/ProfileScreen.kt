package com.example.buyva.features.profile.profileoptions.view

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import com.example.buyva.features.authentication.signup.viewmodel.LogoutViewModel
import com.example.buyva.ui.theme.Cold
import com.example.buyva.ui.theme.Gray
import com.example.buyva.ui.theme.Sea
import com.example.buyva.ui.theme.Teal
import com.example.buyva.navigation.navbar.NavigationBar
import com.example.buyva.utils.components.ScreenTitle
import com.example.buyva.utils.constants.CURRENCY_RATE
import com.example.buyva.utils.constants.CURRENCY_UNIT
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    logoutViewModel: LogoutViewModel,
    onFavClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    onCurrencyClick: () -> Unit = {}
) {
    Log.d("1", "${SharedPreferenceImpl.getFromSharedPreferenceInGeneral(CURRENCY_UNIT)}")
    Log.d("1", "${SharedPreferenceImpl.getLongFromSharedPreferenceInGeneral(CURRENCY_RATE)}")
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
            text = "Hello, ${FirebaseAuth.getInstance().currentUser?.displayName}",
            fontStyle = FontStyle.Italic,
            fontSize = 18.sp,
            color = Cold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileOption(Icons.Default.LocalShipping, "My Orders", Sea, onOrdersClick)
            ProfileOption(Icons.Default.FavoriteBorder, "My Wishlist", Sea, onFavClick)
            ProfileOption(Icons.Default.LocationOn, "Delivery Address", Sea, onAddressClick)
            ProfileOption(Icons.Default.Money, "Currency", Sea, onCurrencyClick)
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedButton(
            onClick = {
                logoutViewModel.logout()
                onLoggedOut()
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Sea),
            border = BorderStroke(1.dp, Sea)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
            Spacer(Modifier.width(8.dp))
            Text("Log Out", fontWeight = FontWeight.SemiBold)
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

