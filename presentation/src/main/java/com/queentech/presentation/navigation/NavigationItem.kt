package com.queentech.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.ui.graphics.vector.ImageVector
import com.queentech.presentation.navigation.RouteName.CAMERA
import com.queentech.presentation.navigation.RouteName.INFORMATION
import com.queentech.presentation.navigation.RouteName.LOGIN
import com.queentech.presentation.navigation.RouteName.LOTTO_NUMBER
import com.queentech.presentation.navigation.RouteName.MY_PAGE
import com.queentech.presentation.navigation.RouteName.SIGNUP
import com.queentech.presentation.navigation.RouteName.STATISTIC

sealed class MainNav(
    override val route: String,
    override val title: String,
    val icon: ImageVector
) : Destination {

    data object Camera : MainNav(CAMERA, "Camera", Icons.Filled.CameraAlt)
    data object LottoNumber : MainNav(LOTTO_NUMBER, "Lotto Number", Icons.Filled.LooksOne)
    data object Information : MainNav(INFORMATION, "Information", Icons.Filled.Home)
    data object Statistic : MainNav(STATISTIC, "Statistic", Icons.Filled.AutoGraph)
    data object MyPage : MainNav(MY_PAGE, "My page", Icons.Filled.AccountCircle)

    companion object {
        fun isMainRoute(route: String?): Boolean {
            return when (route) {
                CAMERA, INFORMATION, STATISTIC, LOTTO_NUMBER, MY_PAGE -> true
                else -> false
            }
        }
    }
}

object LoginNav : Destination {
    override val route: String = LOGIN
    override val title: String = "Login"
}

object SignUpNav : Destination {
    override val route: String = SIGNUP
    override val title: String = "SignUp"
}

interface Destination {
    val route: String
    val title: String
}

object RouteName {
    const val LOGIN = "LoginScreen"
    const val SIGNUP = "SignUpScreen"
    const val CAMERA = "CameraScreen"
    const val INFORMATION = "InformationScreen"
    const val STATISTIC = "StatisticScreen"
    const val MY_PAGE = "MyPageScreen"
    const val LOTTO_NUMBER = "LottoNumberScreen"
}
