package com.caucapstone.app.view

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.caucapstone.app.R
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.data.room.Image
import com.caucapstone.app.util.NestedNavItem
import com.caucapstone.app.viewmodel.FileViewModel
import java.time.format.DateTimeFormatter

private const val DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

@Composable
fun FileScreen(
    onNavigate: (String) -> Unit,
    viewModel: FileViewModel = hiltViewModel()
) {
    val items = viewModel.getImages().collectAsState(emptyList()).value

    // Normal layer
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = globalPaddingValue, end = globalPaddingValue)
    ) {
        // Image(viewModel.bitmap.asImageBitmap(), null)
        /*
        LazyColumn() {
            items(5) { item ->
                if (item == 0) Box(modifier = Modifier.height(25.dp)) else null
                ImageItemCard({})
            }
        }
         */
        if (items.isEmpty()) {
            UniversalIndicator(
                Icons.Filled.ErrorOutline,
                stringResource(R.string.indicator_no_image)
            )
        }
        else {
            LazyColumn() {
                itemsIndexed(items) { index, item ->
                    if (index == 0) Box(modifier = Modifier.height(25.dp))
                    ImageItemCard(item, {}, index == items.size)
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
            onClick = { onNavigate(NestedNavItem.ImageAddScreenItem.route) }
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
        }
        Box(modifier = Modifier.width(10.dp))
        FloatingActionButton(
            onClick = { viewModel.testFunc() }
        ) {
            Icon(Icons.Filled.DeveloperMode, contentDescription = null)
        }
    }
}

@Composable
fun ImageItemCard(
    image: Image,
    clickable: () -> Unit,
    isLastItem: Boolean = false
) {
    Column() {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .paint(
                    rememberAsyncImagePainter(image.uri),
                    contentScale = ContentScale.FillWidth
                )
                .padding(bottom = if (isLastItem) 0.dp else 25.dp)
                .clickable { clickable() }
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                Text(
                    image.caption,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    image.localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        if (!isLastItem) Box(modifier = Modifier.height(25.dp))
    }
}