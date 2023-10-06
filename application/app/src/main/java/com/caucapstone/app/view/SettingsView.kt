package com.caucapstone.app.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        content
    ) {
        Text("This is SettingsView")
    }
}