package com.caucapstone.app.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.util.NavItem
import com.caucapstone.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    Scaffold(bottomBar = { BottomBar(navController, viewModel) }) {
        /*
        Column(modifier = Modifier.fillMaxSize().padding(globalPaddingValue)) {
            Surface(modifier = Modifier.padding(it)) {
                Text(
                    text = viewModel.output.value
                )
            }
            Box(modifier = Modifier.height(20.dp))
            Button(onClick = { viewModel.init() }) {
                Text("Run python codes")
            }
        }

         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = it.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainNavContainer(navController)
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController,
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
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, color = MaterialTheme.colorScheme.onBackground) },
                selected = viewModel.navControllerState.value == index,
                onClick = {
                    viewModel.setNavControllerState(index)
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun MainNavContainer(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "/file",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("/camera") { CameraView() }
        composable("/file") { FileView() }
        composable("/setting") { SettingView() }
    }
}