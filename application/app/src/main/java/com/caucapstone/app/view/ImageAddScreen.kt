package com.caucapstone.app.view

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.caucapstone.app.R
import com.caucapstone.app.viewmodel.ImageAddViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageAddScreen(
    onNavigateBack: () -> Unit,
    viewModel: ImageAddViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri.value = uri }
    )

    Scaffold(
        topBar = {
            UniversalTopAppBar(
                title = { Text(stringResource(R.string.screen_name_image_add)) },
                onClick = onNavigateBack,
                actions = {
                    IconButton(onClick = {
                        if (imageUri.value != null) {
                            val id = viewModel.getUUID()
                            saveImageToInternalStorage(
                                context = context,
                                uri = imageUri.value!!,
                                id = id
                            )
                            viewModel.addImageToDatabase(
                                id = id,
                                caption = viewModel.caption.value
                            )
                            onNavigateBack()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Filled.Add, null)
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
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 15.dp)
                    .verticalScroll(scrollState)
            ) {
                // Image loading area
                if (imageUri.value == null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(shape = RoundedCornerShape(15.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ImageNotSupported,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri.value),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 500.dp)
                            .clip(shape = RoundedCornerShape(15.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
                Box(modifier = Modifier.height(15.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    //PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    onClick = { launcher.launch("image/*") }
                ) {
                    Icon(Icons.Filled.ImageSearch, contentDescription = null)
                    Box(modifier = Modifier.width(10.dp))
                    Text(stringResource(R.string.string_find_image_button))
                }
                Box(modifier = Modifier.height(25.dp))

                // Text field area
                OutlinedTextField(
                    value = viewModel.caption.value,
                    onValueChange = { newValue -> viewModel.setCaption(newValue) },
                    label = { Text(stringResource(R.string.string_image_caption)) },
                    singleLine = true,
                    enabled = true,
                    maxLines = 1,
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun saveImageToInternalStorage(
    context: Context,
    uri: Uri,
    id: String
) {
    val inputStream = context.contentResolver.openInputStream(uri)
    val outputStream = context.openFileOutput("${id}.jpg", Context.MODE_PRIVATE)
    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
}