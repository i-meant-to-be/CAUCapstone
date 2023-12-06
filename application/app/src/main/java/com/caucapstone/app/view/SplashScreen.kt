package com.caucapstone.app.view

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.caucapstone.app.ColorBlindType
import com.caucapstone.app.R
import com.caucapstone.app.util.NestedNavItem
import com.caucapstone.app.viewmodel.SplashViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashScreen(
    onNavigate: (String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val cameraPermissionState: PermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val notificationPermissionState: PermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (!cameraPermissionState.status.isGranted) cameraPermissionState.launchPermissionRequest()
            if (!notificationPermissionState.status.isGranted) notificationPermissionState.launchPermissionRequest()
            viewModel.data.collect { settingProto ->
                if (settingProto.colorBlindType in listOf(ColorBlindType.UNRECOGNIZED, ColorBlindType.COLOR_BLIND_NONE)) {
                    onNavigate(NestedNavItem.SelectColorBlindTypeItem.route)
                } else {
                    onNavigate(NestedNavItem.MainScreenItem.route)
                }
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )
    }
}