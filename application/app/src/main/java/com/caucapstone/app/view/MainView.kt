package com.caucapstone.app.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caucapstone.app.data.MainBottomBarItemData
import com.caucapstone.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(viewModel: MainViewModel) {
    val navController = rememberNavController()

    Scaffold(bottomBar = { BottomBar() }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 10.dp, start = 10.dp, end = 10.dp,
                    bottom = it.calculateBottomPadding()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainNavContainer(navController)

            /*
            Surface() {
                Text(
                    text = viewModel.output.value
                )
            }
            Box(modifier = Modifier.height(20.dp))
            Button(onClick = { viewModel.init() }) {
                Text("Run python codes")
            }

             */
        }
    }
}

@Composable
fun BottomBar() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        MainBottomBarItemData("Camera", Icons.Filled.CameraAlt),
        MainBottomBarItemData("File", Icons.Filled.FileCopy)
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, color = MaterialTheme.colorScheme.onBackground) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}

@Composable
fun MainNavContainer(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "/camera"
    ) {
        composable("/camera") { CameraView(navController) }
        composable("/file") { FileView(navController) }
    }
}