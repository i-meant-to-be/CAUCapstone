package com.caucapstone.app.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.caucapstone.app.viewmodel.ImageViewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewScreen(
    onNavigate: () -> Unit,
    imageId: String,
    viewModel: ImageViewViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    if (viewModel.dialogState.value) {
        DeleteDialog(
            onConfirm = { viewModel.deleteImage(imageId) },
            onDismissRequest = { viewModel.setDialogState(false) }
        )
    }

    Scaffold(
        topBar = {
            UniversalTopAppBar(
                title = {  },
                onClick = onNavigate,
                actions = {
                    IconButton(onClick = {

                    }) {
                        Icon(Icons.Filled.Edit, null)
                    }
                    IconButton(onClick = {
                        viewModel.setDialogState(true)
                    }) {
                        Icon(Icons.Filled.Delete, null)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.TopStart
        ) {
            ImageViewImageBox(context, imageId)
            ImageViewBottomBar()
        }
    }
}

@Composable
fun ImageViewImageBox(
    context: Context,
    id: String
) {
    val path = "${context.filesDir}/${id}.jpg"
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(path)
            .build()
    )
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ImageViewBottomBar() {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {

    }
}

@Composable
fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Text("사진을 삭제하실 건가요?")
        },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                onConfirm()
            }) {
                Text("네")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("아니오")
            }
        }
    )
}