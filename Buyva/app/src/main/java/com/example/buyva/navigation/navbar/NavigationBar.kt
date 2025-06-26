package com.example.buyva.navigation.navbar

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.example.buyva.ui.theme.Cold
import kotlinx.coroutines.flow.MutableStateFlow
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView



object NavigationBar {
    val route = MutableLiveData<Int>(0)
    val mutableNavBarState = MutableStateFlow(true)
    @Composable
    fun ShowCurvedNavBar(navController: NavHostController) {
        AndroidView(
            factory = { context ->
                CurvedBottomNavigationView(context).apply {
                    unSelectedColor = Color.White.toArgb()
                    selectedColor = Cold.toArgb()
                    navBackgroundColor = Cold.toArgb()
                    val cbnMenuItems = NavigationBarItem.menuItems.map { screen ->
                        CbnMenuItem(
                            icon = screen.icon,
                            avdIcon = screen.selectedIcon,
                            destinationId = screen.id
                        )
                    }

                    layoutDirection = View.LAYOUT_DIRECTION_LTR
                    setMenuItems(cbnMenuItems.toTypedArray(), 0)
                    setOnMenuItemClickListener { cbnMenuItem, i ->
                        route.value = i
                        navController.popBackStack()
                        navController.navigate(NavigationBarItem.menuItems[i].route)
                    }
                    route.value?.let { setMenuItems(cbnMenuItems.toTypedArray(), it) }

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(color = Color.Transparent)
        )

    }

}