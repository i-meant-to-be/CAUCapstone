package com.caucapstone.app.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.R

/*
### Example codes

val itemsList = (0..5).toList()
val itemsIndexedList = listOf("A", "B", "C")

val itemModifier = Modifier.border(1.dp, Color.Blue).height(80.dp).wrapContentSize()

LazyVerticalGrid(
    columns = GridCells.Fixed(3)
) {
    items(itemsList) {
        Text("Item is $it", itemModifier)
    }
    item {
        Text("Single item", itemModifier)
    }
    itemsIndexed(itemsIndexedList) { index, item ->
        Text("Item at index $index is $item", itemModifier)
    }
}
 */

@Composable
fun FileView() {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(globalPaddingValue)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(6) { item ->
                Card(
                    modifier = Modifier.size(110
                        .dp)
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
    }
}