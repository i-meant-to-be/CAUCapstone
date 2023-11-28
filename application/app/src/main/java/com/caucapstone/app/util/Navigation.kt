package com.caucapstone.app.util

import android.util.Log
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
import com.caucapstone.app.view.MainScreenNavItem
import com.caucapstone.app.view.SplashScreen

fun NavController.navigateBack() {
    if (currentBackStack.value.size > 2) {
        popBackStack()
    }
}

fun NavController.navigateBackToRoot() {
    navigate(NestedNavItem.MainScreenItem.route) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}

fun NavController.navigateBackToRoot(route: String) {
    navigate(route) {
        popUpTo(graph.id) {
            inclusive = true
        }
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
        composable(
            route = NestedNavItem.SplashScreenItem.route
        ) {
            SplashScreen (
                onNavigate = { navHostController.navigate(NestedNavItem.MainScreenItem.route) }
            )
        }

        composable(
            route = NestedNavItem.DevScreenItem.route
        ) {}

        composable(
            route = NestedNavItem.CameraViewScreenItem.route
        ) {}

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
                onBackNavigate = { navHostController.navigateBack() },
                onNavigateBackToRoot = { navHostController.navigateBackToRoot("${NestedNavItem.MainScreenItem.route}?destRoute=${MainScreenNavItem.FileNavItem.route}") },
                id = imageId ?: ""
            )
        }

        composable(
            route = NestedNavItem.ImageAddScreenItem.route
        ) {
            ImageAddScreen(
                onNavigateBack = { navHostController.navigateBack() }
            )
        }

        composable(
            route = "${NestedNavItem.MainScreenItem.route}?destRoute={destRoute}",
            arguments = listOf(
                navArgument("destRoute") {
                    nullable = true
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val destRoute = entry.arguments!!.getString("destRoute")
            Log.e("CAUCAPSTONE", destRoute.toString())
            MainScreen(
                onNavigate = { route -> navHostController.navigate(route) },
                route = destRoute ?: MainScreenNavItem.CameraNavItem.route
            )
        }
    }
}