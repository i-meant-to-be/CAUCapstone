package com.caucapstone.app.view

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.R

@Composable
fun FileView() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> 
            imageUri = uri
        }

    // Normal layer
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = globalPaddingValue, end = globalPaddingValue)
    ) {
        /*
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(6) { item ->
                Card(
                    modifier = Modifier.size(110.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Text("Item is $item")
                        Image(
                            painter = painterResource(id = R.drawable.test),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
         */

        LazyColumn() {
            items(5) { item ->
                if (item == 0) Box(modifier = Modifier.height(25.dp)) else null
                ImageItemCard()
            }
        }
    }

    // FloatingActionButton layer
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(globalPaddingValue)
    ) {
        FloatingActionButton(
            onClick = { launcher.launch("image/*") }
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
        }
    }
}

@Composable
fun ImageItemCard(isLastItem: Boolean = false) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(bottom = if (isLastItem) 0.dp else 25.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(
                "Caption",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Datetime",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}