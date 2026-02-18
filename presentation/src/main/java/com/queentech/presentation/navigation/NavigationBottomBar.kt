package com.queentech.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.queentech.presentation.util.NavigationHelper

@Composable
fun NavigationBottomBar(
    navController: NavHostController,
    currentRoute: String?
) {
    val mainBottomNavigationItems = listOf(
        MainNav.Camera,
        MainNav.ExpectNumber,
        MainNav.Information,
        MainNav.Statistic,
        MainNav.MyPage,
    )

    Column {
        HorizontalDivider(thickness = 2.dp)
        NavigationBar(containerColor = Color.White) {
            mainBottomNavigationItems.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                    selected = currentRoute == item.route,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    label = { Text(text = item.title, style = MaterialTheme.typography.labelMedium) },
                    onClick = {
                        NavigationHelper.navigate(
                            navController,
                            item.route,
                            navController.graph.startDestinationRoute
                        )
                    }
                )
            }
        }
    }
}