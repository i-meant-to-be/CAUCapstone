package com.caucapstone.app.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
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