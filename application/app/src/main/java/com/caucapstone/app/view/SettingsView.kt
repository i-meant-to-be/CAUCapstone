package com.caucapstone.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.caucapstone.app.data.globalPaddingValue

@Composable
fun SettingView() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(globalPaddingValue)
    ) {
        items(5) { item ->
            SwitchSettingViewItem("설정 항목 $item", "패턴 필터의 투명도를 조정합니다.", item == 5)
        }
        items(5) { item ->
            TextFieldSettingViewItem("설정 항목 $item", "패턴 필터의 투명도를 조정합니다.", item == 5)
        }
    }
}

@Composable
fun SwitchSettingViewItem(
    itemName: String,
    itemDescription: String = "",
    isLastItem: Boolean = false
) {
    val checked = remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLastItem) 0.dp else 20.dp)
    ) {

        Column() {
            Text(itemName, style = MaterialTheme.typography.titleLarge)
            Text(itemDescription, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(
            checked = checked.value,
            onCheckedChange = { checked.value = !checked.value },
            thumbContent = if (checked.value) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            } else {
                null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldSettingViewItem(
    itemName: String,
    itemDescription: String = "",
    isLastItem: Boolean = false
) {
    val text = remember { mutableStateOf("") }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLastItem) 0.dp else 20.dp)
    ) {

        Column() {
            Text(itemName, style = MaterialTheme.typography.titleLarge)
            Text(itemDescription, style = MaterialTheme.typography.bodyMedium)
        }
        OutlinedTextField(
            modifier = Modifier.size(width = 50.dp, height = 20.dp),
            shape = RoundedCornerShape(10.dp),
            value = text.value,
            textStyle = MaterialTheme.typography.bodySmall,
            onValueChange = { text.value = it }
        )
    }
}