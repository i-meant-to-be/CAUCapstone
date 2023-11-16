package com.caucapstone.app.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalTopAppBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit
) {
    TopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}