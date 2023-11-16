package com.caucapstone.app.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caucapstone.app.R
import com.caucapstone.app.view.MainScreen
import com.caucapstone.app.view.SplashScreen

sealed class NavItem(
    val title: Int,
    val icon: ImageVector,
    val route: String
) {
    object MainNavItem : NavItem(0, Icons.Filled.Apps, "/")
    object CameraNavItem : NavItem(R.string.main_nav_bar_name_camera, Icons.Filled.CameraAlt,"/camera")
    object FileNavItem : NavItem(R.string.main_nav_bar_name_file, Icons.Filled.FileCopy,"/file")
    object SettingNavItem : NavItem(R.string.main_nav_bar_name_setting, Icons.Filled.Settings,"/setting")
}

sealed class NestedNavItem(
    val route: String
) {
    object SplashScreenItem : NestedNavItem("/Splash")
    object DevScreenItem : NestedNavItem("/Dev")
    object CameraViewScreenItem : NestedNavItem("/CameraView")
    object ImageViewScreenItem : NestedNavItem("/ImageView")
    object ImageAddScreenItem : NestedNavItem("/ImageAdd")

    object MainScreenItem : NestedNavItem("/Main") {
        object CameraScreenItem : NestedNavItem("/Main/Camera")
        object FileScreenItem : NestedNavItem("/Main/File")
        object SettingScreenItem : NestedNavItem("/Main/Setting")
    }
}

@Composable
fun NestedNav() {
    val navHostController = rememberNavController()

    NavHost(
        navController = navHostController,
        startDestination = NestedNavItem.SplashScreenItem.route
    ) {
        composable(NestedNavItem.SplashScreenItem.route) {
            SplashScreen { navHostController.navigate(NestedNavItem.MainScreenItem.route) }
        }
        composable(NestedNavItem.DevScreenItem.route) {}
        composable(NestedNavItem.CameraViewScreenItem.route) {}
        composable(NestedNavItem.ImageViewScreenItem.route) {}
        composable(NestedNavItem.ImageAddScreenItem.route) {}
        composable(NestedNavItem.MainScreenItem.route) { MainScreen() }
    }
}