package com.caucapstone.app.view

import android.Manifest
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.caucapstone.app.view.camera.CameraContent
import com.caucapstone.app.view.camera.NoPermissionScreen
import com.caucapstone.app.viewmodel.CameraViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(viewModel: CameraViewModel = hiltViewModel()){
    val cameraPermissionState: PermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = viewModel.snackbarHostState)
        }
    ) {
        MainContent(
            paddingValues = it,
            hasPermission = cameraPermissionState.status.isGranted,
            onRequestPermission = cameraPermissionState::launchPermissionRequest
        )
    }
}

@Composable
private fun MainContent(
    paddingValues: PaddingValues,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    if (hasPermission) {
        CameraContent(paddingValues)
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}