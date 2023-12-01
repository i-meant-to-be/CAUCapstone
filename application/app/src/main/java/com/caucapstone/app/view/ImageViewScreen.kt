package com.caucapstone.app.view

import android.content.Context
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.caucapstone.app.R
import com.caucapstone.app.SettingProto
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.data.room.Image
import com.caucapstone.app.util.createNotificationChannel
import com.caucapstone.app.util.showSimpleNotification
import com.caucapstone.app.viewmodel.ImageViewViewModel
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
    val data = viewModel.settingFlow.collectAsState(SettingProto.getDefaultInstance()).value

    val notificationTitle = stringResource(R.string.notification_processing_image_title)
    val notificationText = stringResource(R.string.notification_processing_image_text)

    LaunchedEffect(key1 = 1) {
        createNotificationChannel("NotificationChannel", context)
        // showSimpleNotification(context, "NotificationChannel", 0, "Title", "Content")
    }

    if (viewModel.dialogState.value == 1) {
        UniversalDialog(
            onDismissRequest = { viewModel.setDialogState(0) },
            text = { Text(stringResource(R.string.dialog_delete_image)) },
            onConfirm = {
                viewModel.deleteImageOnDatabase(id)
                viewModel.deleteImageOnStorage("${context.filesDir}/${image.id}.png")
                onNavigateBackToRoot()
            },
            onDismiss = {

            }
        )
    }

    if (viewModel.dialogState.value == 2) {
        UniversalDialog(
            onDismissRequest = {  },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator()
                    Box(modifier = Modifier.height(20.dp))
                    Text(stringResource(R.string.dialog_processing_image))
                }
            }
        )
    }

    if (viewModel.dialogState.value == 3) {
        UniversalDialog(
            onDismissRequest = { viewModel.setDialogState(0) },
            text = {
                OutlinedTextField(
                    value = viewModel.imageEditTextFieldValue.value,
                    onValueChange = { newValue -> viewModel.setImageEditTextFieldValue(newValue) },
                    label = { Text(stringResource(R.string.string_edit_image_caption)) },
                    singleLine = true,
                    enabled = true,
                    maxLines = 1,
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            onConfirm = {
                viewModel.updateImage(image.copy(caption = viewModel.imageEditTextFieldValue.value))
            },
            confirmButtonLabel = "수정"
        )
    }

    Scaffold(
        topBar = {
            UniversalTopAppBar(
                title = {  },
                onClick = onBackNavigate,
                actions = {
                    IconButton(onClick = { viewModel.shareImage(context, path) }) {
                        Icon(Icons.Filled.Share, null)
                    }
                    IconButton(onClick = { viewModel.setDialogState(3) }) {
                        Icon(Icons.Filled.Edit, null)
                    }
                    IconButton(onClick = { viewModel.setDialogState(1) }) {
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
                onTap = { offset -> viewModel.setOffset(offset) },
                modifier = Modifier.fillMaxWidth()
            )
            ImageViewBottomBar(
                expanded = viewModel.bottomBarExpanded.value,
                onExpandClick = { viewModel.reverseBottomBarExpanded() },
                onProcessClick = {
                    showSimpleNotification(
                        context = context,
                        channelId = "NotificationChannel",
                        notificationId = 0,
                        textTitle = notificationTitle,
                        textContent = notificationText
                    )
                    viewModel.setDialogState(2)

                    val processedImageId = viewModel.processImage(
                        context = context,
                        originFilePath = path,
                        docMode = data.docMode,
                        removeGlare = data.removeGlare,
                        colorSensitivity = data.colorSensitivity
                    )
                    viewModel.addImageToDatabase(
                        Image(
                            id = processedImageId,
                            caption = "(윤곽선 처리) ${image.caption}",
                            originId = id,
                            canBeProcessed = false
                        )
                    )
                    viewModel.updateImage(image.copy(canBeProcessed = false))
                },
                image = if (!viewModel.isImageDeleted.value) image else Image.getDefaultInstance(),
                buttonEnabled = if (!viewModel.isImageDeleted.value) image.canBeProcessed else false
            )
        }
    }
}

@Composable
fun ImageViewImage(
    context: Context,
    path: String,
    onTap: (Pair<Float, Float>) -> Unit,
    modifier: Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
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
            contentScale = ContentScale.Fit,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures { offset ->
                    offsetX = offset.x
                    offsetY = offset.y
                    onTap(Pair(offset.x, offset.y))
                }
            }
        )
        // Text("$offsetX, $offsetY")
    }
}

@Composable
fun ImageViewBottomBar(
    expanded: Boolean,
    onExpandClick: () -> Unit,
    onProcessClick: () -> Unit,
    image: Image,
    buttonEnabled: Boolean = true
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
                    title = stringResource(R.string.string_image_caption),
                    value = image.caption
                )
                Box(modifier = Modifier.height(globalPaddingValue))
                ImageViewBottomBarItem(
                    title = stringResource(R.string.string_image_datetime),
                    value = image.localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
                )
                Box(modifier = Modifier.height(globalPaddingValue))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    onClick = onProcessClick,
                    enabled = buttonEnabled
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