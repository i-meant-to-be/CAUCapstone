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
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.caucapstone.app.R

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
    object SplashScreen : NestedNavItem("/Splash")
    object DevScreen : NestedNavItem("/Dev")
    object CameraShootScreen : NestedNavItem("/CameraView")
    object ImageViewScreen : NestedNavItem("/ImageView")
    object ImageAddScreen : NestedNavItem("/ImageAdd")

    object MainScreen : NestedNavItem("/Main") {
        object CameraScreen : NestedNavItem("/Main/Camera")
        object FileScreen : NestedNavItem("/Main/File")
        object SettingScreen : NestedNavItem("/Main/Setting")
    }
}

@Composable
fun NestedNav() {
    val navHostController = rememberNavController()

    NavHost(
        navController = navHostController,
        startDestination = NestedNavItem.SplashScreen.route
    ) {
        composable(NestedNavItem.SplashScreen.route) {}
        composable(NestedNavItem.DevScreen.route) {}
        composable(NestedNavItem.CameraShootScreen.route) {}
        composable(NestedNavItem.ImageViewScreen.route) {}
        composable(NestedNavItem.ImageAddScreen.route) {}
        navigation(
            route = NestedNavItem.MainScreen.route,
            startDestination = NestedNavItem.MainScreen.CameraScreen.route
        ) {
            composable(NestedNavItem.MainScreen.CameraScreen.route) {}
            composable(NestedNavItem.MainScreen.FileScreen.route) {}
            composable(NestedNavItem.MainScreen.SettingScreen.route) {}
        }
    }
}