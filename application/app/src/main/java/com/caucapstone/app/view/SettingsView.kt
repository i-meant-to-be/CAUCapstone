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
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.caucapstone.app.SettingProto
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.util.SettingItem
import com.caucapstone.app.util.SettingType
import com.caucapstone.app.viewmodel.SettingViewModel
import kotlin.math.roundToInt

@Composable
fun SettingView(viewModel: SettingViewModel = hiltViewModel()) {
    val data = viewModel.settingsFlow.collectAsState(initial = SettingProto.getDefaultInstance()).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(globalPaddingValue)
    ) {
        SwitchSettingViewItem(
            stringResource(SettingItem.CVDocMode.title),
            stringResource(SettingItem.CVDocMode.explanation),
            data.docMode,
            viewModel::setDocMode
        )
        SwitchSettingViewItem(
            stringResource(SettingItem.CVRemoveGlare.title),
            stringResource(SettingItem.CVRemoveGlare.explanation),
            data.removeGlare,
            viewModel::setRemoveGlare
        )
        SliderSettingViewItem(
            stringResource(SettingItem.CVColorSensitivity.title),
            stringResource(SettingItem.CVColorSensitivity.explanation),
            data.colorSensitivity,
            viewModel::setColorSensitivity
        )
    }
}

@Composable
fun SwitchSettingViewItem(
    itemName: String,
    itemDescription: String = "",
    itemValue: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isLastItem: Boolean = false
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLastItem) 0.dp else 25.dp)
    ) {
        Column(modifier = Modifier.width((0.75 * screenWidth).dp)) {
            Text(itemName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Box(modifier = Modifier.height(5.dp))
            Text(itemDescription, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(
            checked = itemValue,
            onCheckedChange = onCheckedChange,
            thumbContent = if (itemValue) {
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
    itemValue: Int,
    onValueChanged: (Int) -> Unit,
    isLastItem: Boolean = false
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val text = remember { mutableStateOf("") }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLastItem) 0.dp else 25.dp)
    ) {
        Column(modifier = Modifier.width((0.75 * screenWidth).dp)) {
            Text(itemName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderSettingViewItem(
    itemName: String,
    itemDescription: String = "",
    itemValue: Int,
    onValueChange: (Int) -> Unit,
    isLastItem: Boolean = false
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLastItem) 0.dp else 25.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                itemName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Box(modifier = Modifier.height(5.dp))
            Text(
                itemDescription,
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = itemValue.toFloat(),
                onValueChange = { onValueChange(it.roundToInt()) },
                steps = 11,
                valueRange = -5f..5f
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 5.dp)) {
                Text(
                    "$itemValue",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}