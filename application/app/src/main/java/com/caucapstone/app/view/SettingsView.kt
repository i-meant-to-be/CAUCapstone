package com.caucapstone.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.util.SettingItem
import com.caucapstone.app.util.SettingType

@Composable
fun SettingView() {
    val items = listOf(
        SettingItem.CVDocMode,
        SettingItem.CVRemoveGlare,
        SettingItem.CVColorSensitivity,
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(globalPaddingValue)
    ) {
        itemsIndexed(items) { _, item ->
            when (item.type) {
                SettingType.BOOL -> SwitchSettingViewItem(
                    stringResource(item.title),
                    stringResource(item.explanation)
                )
                SettingType.UINT -> TextFieldSettingViewItem(
                    stringResource(item.title),
                    stringResource(item.explanation)
                )
                SettingType.INT -> TextFieldSettingViewItem(
                    stringResource(item.title),
                    stringResource(item.explanation)
                )
                else -> SwitchSettingViewItem(
                    stringResource(item.title),
                    stringResource(item.explanation)
                )
            }
        }
    }
}

@Composable
fun SwitchSettingViewItem(
    itemName: String,
    itemDescription: String = "",
    isLastItem: Boolean = false
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val checked = remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLastItem) 0.dp else 20.dp)
    ) {

        Column(modifier = Modifier.width((0.75 * screenWidth).dp)) {
            Text(itemName, style = MaterialTheme.typography.titleLarge)
            Box(modifier = Modifier.height(5.dp))
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
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val text = remember { mutableStateOf("") }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLastItem) 0.dp else 20.dp)
    ) {

        Column(modifier = Modifier.width((0.75 * screenWidth).dp)) {
            Text(itemName, style = MaterialTheme.typography.titleLarge)
            Box(modifier = Modifier.height(5.dp))
            Text(itemDescription, style = MaterialTheme.typography.bodyMedium)
        }
        OutlinedTextField(
            value = text.value,
            shape = RoundedCornerShape(20.dp),
            onValueChange = { text.value = it }
        )
    }
}