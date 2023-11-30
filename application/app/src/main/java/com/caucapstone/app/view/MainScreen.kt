package com.caucapstone.app.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caucapstone.app.R
import com.caucapstone.app.util.navigateBackToRoot
import com.caucapstone.app.viewmodel.MainViewModel

sealed class MainScreenNavItem(
    val title: Int,
    val icon: ImageVector,
    val route: String
) {
    object CameraNavItem : MainScreenNavItem(R.string.main_nav_bar_name_camera, Icons.Filled.CameraAlt,"/Camera")
    object FileNavItem : MainScreenNavItem(R.string.main_nav_bar_name_file, Icons.Filled.FileCopy,"/File")
    object SettingNavItem : MainScreenNavItem(R.string.main_nav_bar_name_setting, Icons.Filled.Settings,"/Setting")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigate: (String) -> Unit,
    route: String = "",
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    when (route) {
        MainScreenNavItem.CameraNavItem.route -> viewModel.setNavControllerState(0)
        MainScreenNavItem.FileNavItem.route -> viewModel.setNavControllerState(1)
        MainScreenNavItem.SettingNavItem.route -> viewModel.setNavControllerState(2)
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                onItemClicked = { route -> navController.navigateBackToRoot(route) },
                viewModel = viewModel
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = it.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainNavContainer(
                navController = navController,
                onNavigate = onNavigate,
                startDestination = route.ifEmpty { MainScreenNavItem.CameraNavItem.route })
        }
    }
}

@Composable
fun BottomBar(
    onItemClicked: (String) -> Unit,
    viewModel: MainViewModel
) {
    val items = listOf(
        MainScreenNavItem.CameraNavItem,
        MainScreenNavItem.FileNavItem,
        MainScreenNavItem.SettingNavItem
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = stringResource(item.title)) },
                label = { Text(stringResource(item.title), color = MaterialTheme.colorScheme.onBackground) },
                selected = viewModel.navControllerState.value == index,
                onClick = {
                    viewModel.setNavControllerState(index)
                    onItemClicked(item.route)
                }
            )
        }
    }
}

@Composable
fun MainNavContainer(
    navController: NavHostController,
    onNavigate: (String) -> Unit,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(MainScreenNavItem.CameraNavItem.route) { CameraScreen() }
        composable(MainScreenNavItem.FileNavItem.route) { FileScreen(onNavigate) }
        composable(MainScreenNavItem.SettingNavItem.route) { SettingScreen() }
    }
}
