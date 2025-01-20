package com.queentech.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.queentech.presentation.login.LoginScreen
import com.queentech.presentation.main.camera.CameraScreen
import com.queentech.presentation.main.information.InformationScreen
import com.queentech.presentation.main.lottonumber.LottoNumberScreen
import com.queentech.presentation.main.mypage.MyPageScreen
import com.queentech.presentation.main.statistic.StatisticScreen
import com.queentech.presentation.signup.SignUpScreen

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentRoute = navBackStackEntry?.destination?.route

    Surface {
        Scaffold(
            content = { paddingValues ->
                NavHost(
                    modifier = Modifier.padding(paddingValues),
                    navController = navController,
                    startDestination = MainNav.Information.route
                ) {
                    composable(route = MainNav.Information.route) {
                        InformationScreen()
                    }
                    composable(route = MainNav.Camera.route) {
                        CameraScreen()
                    }
                    composable(route = MainNav.LottoNumber.route) {
                        LottoNumberScreen()
                    }
                    composable(route = MainNav.MyPage.route) {
                        MyPageScreen()
                    }
                    composable(route = LoginNav.route) {
                        LoginScreen(navController)
                    }
                    composable(route = SignUpNav.route) {
                        SignUpScreen()
                    }
                    composable(route = MainNav.Statistic.route) {
                        StatisticScreen()
                    }
                }
            },
            bottomBar = {
                if (MainNav.isMainRoute(currentRoute)) {
                    NavigationBottomBar(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        )
    }
}