package com.queentech.fisherlotto.navigation

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.queentech.fisherlotto.utils.PermissionState
import com.queentech.fisherlotto.utils.PermissionHelper
import com.queentech.fisherlotto.utils.permissionRequest
import com.queentech.presentation.login.LoginScreen
import com.queentech.presentation.login.AccountRecoveryScreen
import com.queentech.presentation.login.SignUpScreen
import com.queentech.presentation.main.camera.CameraScreen
import com.queentech.presentation.main.expect_number.ExpectNumberScreen
import com.queentech.presentation.main.home.HomeScreen
import com.queentech.presentation.main.mypage.MyPageScreen
import com.queentech.presentation.main.statistic.StatisticScreen

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentRoute = navBackStackEntry?.destination?.route

    BackHandler(
        enabled = currentRoute != MainNav.Home.route && MainNav.isMainRoute(currentRoute),
    ) {
        navController.popBackStack(MainNav.Home.route, inclusive = false)
    }

    val normalPermissions = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val normalPermissionState = permissionRequest(
        permissions = normalPermissions,
        rationaleTitle = "권한 요청",
        rationaleText = "권한 요청을 수락해주시길 바랍니다."
    )

    if (normalPermissionState == PermissionState.Granted) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                content = { paddingValues ->
                    NavHost(
                        modifier = Modifier.padding(paddingValues),
                        navController = navController,
                        startDestination = LoginNav.route
                    ) {
                        composable(route = MainNav.Home.route) {
                            HomeScreen()
                        }
                        composable(route = MainNav.Camera.route) {
                            CameraPermissionScreen()
                        }
                        composable(route = MainNav.ExpectNumber.route) {
                            ExpectNumberScreen()
                        }
                        composable(route = MainNav.MyPage.route) {
                            MyPageScreen(
                                onNavigateToLogin = {
                                    NavigationHelper.navigateToLoginAfterLogout(
                                        navController,
                                        RouteName.LOGIN
                                    )
                                }
                            )
                        }
                        composable(route = LoginNav.route) {
                            LoginScreen(
                                moveToSignUp = {
                                    NavigationHelper.navigate(
                                        navController,
                                        RouteName.SIGNUP
                                    )
                                },
                                moveToAccountRecovery = {
                                    NavigationHelper.navigate(
                                        navController,
                                        RouteName.ACCOUNT_RECOVERY
                                    )
                                },
                                moveToHome = {
                                    navController.navigate(MainNav.Home.route) {
                                        popUpTo(LoginNav.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable(route = SignUpNav.route) {
                            SignUpScreen(popBackStack = { navController.popBackStack() })
                        }
                        composable(route = AccountRecoveryNav.route) {
                            AccountRecoveryScreen(
                                moveToHome = {
                                    navController.navigate(MainNav.Home.route) {
                                        popUpTo(LoginNav.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
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
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun CameraPermissionScreen() {
    val context = LocalContext.current
    val permissionState = permissionRequest(
        permissions = listOf(Manifest.permission.CAMERA),
        rationaleTitle = "카메라 권한 필요",
        rationaleText = "QR 당첨 확인을 위해 카메라 권한이 필요합니다."
    )

    if (permissionState == PermissionState.Granted) {
        CameraScreen()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("QR 당첨 확인을 위해 카메라 권한이 필요합니다.")
                Button(onClick = { PermissionHelper.openAppSettings(context) }) {
                    Text("설정으로 이동")
                }
            }
        }
    }
}
