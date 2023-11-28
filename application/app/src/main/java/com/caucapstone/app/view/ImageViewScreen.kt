package com.caucapstone.app.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.caucapstone.app.R
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.data.room.Image
import com.caucapstone.app.util.createNotificationChannel
import com.caucapstone.app.util.showSimpleNotification
import com.caucapstone.app.viewmodel.ImageViewViewModel
import com.chaquo.python.Python
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.time.format.DateTimeFormatter

private const val DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewScreen(
    onBackNavigate: () -> Unit,
    onNavigateBackToRoot: () -> Unit,
    id: String,
    viewModel: ImageViewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val image = viewModel.getImageById(id)
    val path = if (!viewModel.isImageDeleted.value) "${context.filesDir}/${image.id}.png" else ""
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = 1) {
        createNotificationChannel("NotificationChannel", context)
        showSimpleNotification(context, "NotificationChannel", 0, "Title", "Content")
    }

    if (viewModel.dialogState.value == 1) {
        UniversalDialog(
            onDismissRequest = { viewModel.setDialogState(0) },
            text = { Text(stringResource(R.string.dialog_delete_image)) },
            onConfirm = {
                viewModel.deleteImage(id)
                val imageFile = File(context.filesDir, "${id}.png")
                if (imageFile.exists()) {
                    imageFile.delete()
                    viewModel.setImageDeleted()
                }
                onNavigateBackToRoot()
            }
        )
    }

    Scaffold(
        topBar = {
            UniversalTopAppBar(
                title = {  },
                onClick = onBackNavigate,
                actions = {
                    IconButton(onClick = {
                        val imageUri = FileProvider.getUriForFile(
                            context,
                            "com.caucapstone.app.provider",
                            File(context.filesDir, "${image.id}.png")
                        )
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, imageUri)
                            type = "image/png"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)

                        context.startActivity(shareIntent, null)
                    }) {
                        Icon(Icons.Filled.Share, null)
                    }
                    IconButton(onClick = {

                    }) {
                        Icon(Icons.Filled.Edit, null)
                    }
                    IconButton(onClick = {
                        viewModel.setDialogState(1)
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
                .padding(it)
                .background(MaterialTheme.colorScheme.background)
        ) {
            ImageViewImage(
                context = context,
                path = path,
                modifier = Modifier.fillMaxWidth()
            )
            ImageViewBottomBar(
                expanded = viewModel.bottomBarExpanded.value,
                onExpandClick = { viewModel.reverseBottomBarExpanded() },
                onProcessClick = {
                     viewModel.processImage()
                    /*
                    val imageId = viewModel.getUUID()
                    val outputStream = context.openFileOutput("${imageId}.png", Context.MODE_PRIVATE)
                    val imageProcessRequest: WorkRequest =
                        OneTimeWorkRequestBuilder<ImageProcessWorker>().build()
                    val builder = Data.Builder()
                    builder.putString("KEY_IMAGE_PATH", path)
                    WorkManager.getInstance(context).enqueue(imageProcessRequest)
                    outputStream.close()

                     */
                },
                image = if (!viewModel.isImageDeleted.value) image else Image.getDefaultInstance()
            )
        }
    }
}

@Composable
fun ImageViewImage(
    context: Context,
    path: String,
    modifier: Modifier
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(path)
            .build()
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ImageViewBottomBar(
    expanded: Boolean,
    onExpandClick: () -> Unit,
    onProcessClick: () -> Unit,
    image: Image
) {
    val height = animateDpAsState(
        targetValue = if (expanded) 220.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearOutSlowInEasing
        ),
        label = "ImageView"
    )
    
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .size(height = 40.dp, width = 50.dp)
                    .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(top = 10.dp)
                    .clickable { onExpandClick() }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreHoriz,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height.value)
                    .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(20.dp)
            ) {
                ImageViewBottomBarItem(
                    title = "캡션",
                    value = image.caption
                )
                Box(modifier = Modifier.height(globalPaddingValue))
                ImageViewBottomBarItem(
                    title = "촬영 날짜",
                    value = image.localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
                )
                Box(modifier = Modifier.height(globalPaddingValue))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    onClick = onProcessClick
                ) {
                    Icon(Icons.Filled.PublishedWithChanges, null)
                    Box(modifier = Modifier.width(globalPaddingValue))
                    Text(
                        text = stringResource(R.string.string_image_process),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ImageViewBottomBarItem(
    title: String,
    value: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Box(modifier = Modifier.height(5.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

fun imageProcess(path: String): Bitmap {
    val py = Python.getInstance()
    val module = py.getModule("test")
    val byteArrayOutputStream = ByteArrayOutputStream()
    val bitmap = BitmapFactory.decodeStream(FileInputStream(File(path)))

    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val encodedImage = module.callAttr(
        "test",
        byteArrayOutputStream.toByteArray(),
        bitmap.height,
        bitmap.width
    ).toString().substring(2)
    byteArrayOutputStream.close()
    val imageBytes = Base64.decode(encodedImage, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}