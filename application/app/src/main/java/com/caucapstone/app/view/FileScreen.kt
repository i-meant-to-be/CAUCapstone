package com.caucapstone.app.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.caucapstone.app.R
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.data.room.Image
import com.caucapstone.app.data.roundedCornerShapeValue
import com.caucapstone.app.util.NestedNavItem
import com.caucapstone.app.viewmodel.FileViewModel
import java.time.format.DateTimeFormatter

private const val DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"

@Composable
fun FileScreen(
    onNavigate: (String) -> Unit,
    viewModel: FileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val items = viewModel.getImages()

    if (viewModel.dialogState.value == 1) {
        UniversalDialog(
            onDismissRequest = { viewModel.setDialogState(0) },
            text = { Text(stringResource(R.string.dialog_delete_image_all)) },
            onConfirm = {
                items.forEach { image ->
                    viewModel.deleteImageOnStorage("${context.filesDir}/${image.id}.png")
                }
                viewModel.deleteAllImage()
            },
            onDismiss = {

            }
        )
    }

    if (viewModel.dialogState.value == 2) {
        UniversalDialog(
            onDismissRequest = { viewModel.setDialogState(0) },
            text = { Text("선택한 사진 ${viewModel.imageIdToDelete.size}개를 삭제합니다.") },
            onConfirm = {
                viewModel.imageIdToDelete.forEach { imageId ->
                    viewModel.deleteImageOnStorage("${context.filesDir}/${imageId}.png")
                }
                viewModel.deleteSelectedImageOnDatabase()
            },
            onDismiss = {

            }
        )
    }

    // Normal layer
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = globalPaddingValue, end = globalPaddingValue)
    ) {
        if (items.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                UniversalIndicator(
                    Icons.Filled.ErrorOutline,
                    stringResource(R.string.string_no_image)
                )
            }
        }
        else {
            LazyColumn() {
                itemsIndexed(items) { index, image ->
                    if (index == 0) Box(modifier = Modifier.height(25.dp))
                    ImageItemCard(
                        image = image,
                        onClick = {
                            viewModel.setLongPressEnabled(false)
                            onNavigate("${NestedNavItem.ImageViewScreenItem.route}/${image.id}")
                        },
                        onLongClick = { viewModel.setLongPressEnabled(true) },
                        longPressEnabled = viewModel.longPressEnabled.value,
                        addToList = { viewModel.imageIdToDelete.add(image.id) },
                        removeFromList = { viewModel.imageIdToDelete.remove(image.id) },
                        isLastItem = index == items.size
                    )
                }
            }
        }
    }

    // FloatingActionButton layer
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxSize()
            .padding(globalPaddingValue)
    ) {
        FloatingActionButton(
            onClick = {
                viewModel.setLongPressEnabled(false)
                viewModel.imageIdToDelete.clear()
                onNavigate(NestedNavItem.ImageAddScreenItem.route)
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
        }
        Box(modifier = Modifier.width(5.dp))
        FloatingActionButton(
            onClick = {
                if (viewModel.imageIdToDelete.size < 1) viewModel.setDialogState(1)
                else viewModel.setDialogState(2)
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageItemCard(
    image: Image,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    longPressEnabled: Boolean,
    addToList: () -> Unit,
    removeFromList: () -> Unit,
    isLastItem: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val checked = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val path = "${context.filesDir}/${image.id}.png"
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(path)
            .build()
    )

    Box(modifier = Modifier.padding(bottom = if (isLastItem) 0.dp else 15.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(roundedCornerShapeValue))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                    onLongClick = onLongClick
                )
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (longPressEnabled) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AnimatedCheckbox(
                                checked = checked.value,
                                onCheckedChange = { newValue -> checked.value = newValue },
                                addToList = addToList,
                                removeFromList = removeFromList
                            )
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (image.caption.isNotEmpty()) {
                        Text(
                            image.caption,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(vertical = 3.dp, horizontal = 10.dp)
                        )
                        Box(modifier = Modifier.height(5.dp))
                    }
                    Text(
                        image.localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(vertical = 3.dp, horizontal = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    addToList: () -> Unit,
    removeFromList: () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = expandIn(),
        exit = shrinkOut()
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { newValue ->
                onCheckedChange(newValue)
                if (newValue) { addToList() }
                else { removeFromList() }
            }
        )
    }
}

/*
fun testFunc(path: String): Bitmap {
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

 */