package com.queentech.fisherlotto.navigation

import androidx.navigation.NavHostController

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
}