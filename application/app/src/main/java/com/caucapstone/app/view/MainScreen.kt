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
import com.caucapstone.app.viewmodel.MainViewModel

sealed class NavItem(
    val title: Int,
    val icon: ImageVector,
    val route: String
) {
    object CameraNavItem : NavItem(R.string.main_nav_bar_name_camera, Icons.Filled.CameraAlt,"Camera")
    object FileNavItem : NavItem(R.string.main_nav_bar_name_file, Icons.Filled.FileCopy,"File")
    object SettingNavItem : NavItem(R.string.main_nav_bar_name_setting, Icons.Filled.Settings,"Setting")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigate: (String) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(
                { route -> navController.navigate(route) { popUpTo(navController.graph.id) { inclusive = true } } },
                viewModel
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = it.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainNavContainer(navController, onNavigate)
        }
    }
}

@Composable
fun BottomBar(
    onItemClicked: (String) -> Unit,
    viewModel: MainViewModel
) {
    val items = listOf(
        NavItem.CameraNavItem,
        NavItem.FileNavItem,
        NavItem.SettingNavItem
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
    onNavigate: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = NavItem.CameraNavItem.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(NavItem.CameraNavItem.route) { CameraScreen() }
        composable(NavItem.FileNavItem.route) { FileScreen(onNavigate) }
        composable(NavItem.SettingNavItem.route) { SettingScreen() }
    }
}
