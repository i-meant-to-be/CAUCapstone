package com.caucapstone.app.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.caucapstone.app.view.ImageAddScreen
import com.caucapstone.app.view.ImageViewScreen
import com.caucapstone.app.view.MainScreen
import com.caucapstone.app.view.SplashScreen

fun NavController.navigateBack() {
    if (backQueue.size > 2) {
        popBackStack()
    }
}

sealed class NestedNavItem(
    val route: String
) {
    object SplashScreenItem : NestedNavItem("/Splash")
    object DevScreenItem : NestedNavItem("/Dev")
    object CameraViewScreenItem : NestedNavItem("/CameraView")
    object ImageViewScreenItem : NestedNavItem("/ImageView")
    object ImageAddScreenItem : NestedNavItem("/ImageAdd")
    object MainScreenItem : NestedNavItem("/Main")
}

@Composable
fun Root() {
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
        composable(
            route = "${NestedNavItem.ImageViewScreenItem.route}/{imageId}",
            arguments = listOf(
                navArgument("imageId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val imageId = entry.arguments?.getString("imageId")
            ImageViewScreen(
                onNavigate = {navHostController.navigateBack()},
                imageId = imageId ?: ""
            )
        }
        composable(NestedNavItem.ImageAddScreenItem.route) {
            ImageAddScreen({ navHostController.navigateBack() })
        }
        composable(NestedNavItem.MainScreenItem.route) {
            MainScreen({ route -> navHostController.navigate(route) })
        }
    }
}