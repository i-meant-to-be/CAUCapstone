package com.caucapstone.app.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object MainNavItem : NavItem("Main", Icons.Filled.Apps, "/")
    object CameraNavItem : NavItem("Camera", Icons.Filled.CameraAlt,"/camera")
    object FileNavItem : NavItem("File", Icons.Filled.FileCopy,"/file")
    object SettingNavItem : NavItem("Setting", Icons.Filled.Settings,"/setting")
}