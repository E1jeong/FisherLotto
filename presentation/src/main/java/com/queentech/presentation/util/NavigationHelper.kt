package com.queentech.presentation.util

import androidx.navigation.NavHostController
import com.queentech.presentation.navigation.Destination
import com.queentech.presentation.navigation.LoginNav
import com.queentech.presentation.navigation.MainNav
import com.queentech.presentation.navigation.RouteName
import com.queentech.presentation.navigation.SignUpNav

object NavigationHelper {
    fun navigate(
        controller: NavHostController,
        routeName: String,
        backStackRouteName: String? = null,
        isLaunchSingleTop: Boolean = true,
        needToRestoreState: Boolean = true
    ) {
        controller.navigate(routeName) {
            if (backStackRouteName != null) {
                popUpTo(backStackRouteName) {
                    saveState = true
                }
            }
            launchSingleTop = isLaunchSingleTop
            restoreState = needToRestoreState
        }
    }

    fun findDestination(route: String?): Destination {
        return when {
            route == null -> MainNav.Information
            route.contains(RouteName.CAMERA) -> MainNav.Camera
            route.contains(RouteName.MY_PAGE) -> MainNav.MyPage
            route.contains(RouteName.INFORMATION) -> MainNav.Information
            route.contains(RouteName.LOTTO_NUMBER) -> MainNav.LottoNumber
            route.contains(RouteName.STATISTIC) -> MainNav.Statistic
            route.contains(RouteName.LOGIN) -> LoginNav
            route.contains(RouteName.SIGNUP) -> SignUpNav
            else -> MainNav.Information
        }
    }
}