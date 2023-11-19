package com.caucapstone.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalTopAppBar(
    title: @Composable () -> Unit,
    onClick: () -> Unit,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    TopAppBar(
        title = title,
        navigationIcon = {
            IconButton(
                onClick = onClick
            ) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        },
        actions = actions ?: {},
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}

@Composable
fun UniversalIndicator(
    icon: ImageVector,
    message: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 25.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            message,
            style = MaterialTheme.typography.titleLarge
        )
    }
}