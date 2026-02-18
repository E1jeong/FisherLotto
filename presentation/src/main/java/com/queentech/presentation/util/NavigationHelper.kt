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

    // 로그아웃 전용: back stack 비우고 로그인만 남김
    fun navigateToLoginAfterLogout(controller: NavHostController, loginRoute: String) {
        controller.navigate(loginRoute) {
            // 그래프 루트까지 싹 지움
            popUpTo(controller.graph.startDestinationId) {
                inclusive = true    // startDestination까지 포함해서 제거
            }
            launchSingleTop = true
            restoreState = false    // 이전 상태 복원 X
        }
    }

    fun findDestination(route: String?): Destination {
        return when {
            route == null -> MainNav.Information
            route.contains(RouteName.CAMERA) -> MainNav.Camera
            route.contains(RouteName.MY_PAGE) -> MainNav.MyPage
            route.contains(RouteName.INFORMATION) -> MainNav.Information
            route.contains(RouteName.EXPECT_NUMBER) -> MainNav.ExpectNumber
            route.contains(RouteName.STATISTIC) -> MainNav.Statistic
            route.contains(RouteName.LOGIN) -> LoginNav
            route.contains(RouteName.SIGNUP) -> SignUpNav
            else -> MainNav.Information
        }
    }
}