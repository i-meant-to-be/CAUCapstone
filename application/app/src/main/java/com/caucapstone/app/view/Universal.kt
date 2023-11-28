package com.caucapstone.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.caucapstone.app.R

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
        verticalArrangement = Arrangement.Center
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
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
        )
    }
}

@Composable
fun UniversalDialog(
    onDismissRequest: () -> Unit,
    text: @Composable () -> Unit,
    onConfirm: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    confirmButtonLabel: String? = null,
    dismissButtonLabel: String? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = text,
        confirmButton = {
            if (onConfirm != null) {
                TextButton(onClick = {
                    onDismissRequest()
                    onConfirm()
                }) {
                    Text(confirmButtonLabel ?: stringResource(R.string.dialog_universal_dialog_yes))
                }
            }
        },
        dismissButton = {
            if (onDismiss != null) {
                TextButton(onClick = {
                    onDismissRequest()
                    onDismiss()
                }) {
                    Text(dismissButtonLabel ?: stringResource(R.string.dialog_universal_dialog_no))
                }
            }
        }
    )
}