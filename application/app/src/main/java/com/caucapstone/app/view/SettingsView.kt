package com.caucapstone.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.caucapstone.app.FilterType
import com.caucapstone.app.R
import com.caucapstone.app.SettingProto
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.util.SettingItem
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
        DocModeSettingItem(
            checked = data.docMode,
            onCheckedChange = { newValue -> viewModel.setDocMode(newValue) }
        )
        RemoveGlareSettingItem(
            checked = data.removeGlare,
            onCheckedChange = { newValue -> viewModel.setRemoveGlare(newValue) }
        )
        ColorSensitivitySettingItem(
            value = data.colorSensitivity.toFloat(),
            onValueChange = { newValue -> viewModel.setColorSensitivity(newValue.roundToInt()) },
            steps = 9,
            valueRange = -5f..5f
        )
        FilterTypeSettingItem(
            onClick = { viewModel.filterTypeExpanded.value = true },
            buttonText = {
                val text = when (data.filterType) {
                    FilterType.FILTER_NONE -> stringResource(R.string.filter_name_none)
                    FilterType.FILTER_SPECIFIC -> stringResource(R.string.filter_name_specific)
                    FilterType.FILTER_DALTONIZED -> stringResource(R.string.filter_name_daltonized)
                    FilterType.FILTER_STRIPE -> stringResource(R.string.filter_name_stripe)
                    FilterType.UNRECOGNIZED -> stringResource(R.string.filter_name_none)
                }
                Text(text)
            },
            expanded = viewModel.filterTypeExpanded.value,
            onDismissRequest = { viewModel.filterTypeExpanded.value = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_name_none)) },
                onClick = {
                    viewModel.setFilterMode(FilterType.FILTER_NONE)
                    viewModel.filterTypeExpanded.value = false
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_name_specific)) },
                onClick = {
                    viewModel.setFilterMode(FilterType.FILTER_SPECIFIC)
                    viewModel.filterTypeExpanded.value = false
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_name_stripe)) },
                onClick = {
                    viewModel.setFilterMode(FilterType.FILTER_STRIPE)
                    viewModel.filterTypeExpanded.value = false
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_name_daltonized)) },
                onClick = {
                    viewModel.setFilterMode(FilterType.FILTER_DALTONIZED)
                    viewModel.filterTypeExpanded.value = false
                }
            )
        }
    }
}

@Composable
fun SettingItemSingleLineBackground(
    title: String,
    description: String,
    isLastItem: Boolean = false,
    content: @Composable () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLastItem) 0.dp else 25.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Column(modifier = Modifier.width((0.70 * screenWidth).dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left
                    )
                )
                Box(modifier = Modifier.height(5.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Left
                    )
                )
            }
            content()
        }
    }
}

@Composable
fun SettingItemMultipleLineBackground(
    title: String,
    description: String,
    isLastItem: Boolean = false,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLastItem) 0.dp else 25.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left
                )
            )
            Box(modifier = Modifier.height(5.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Left
                )
            )
            Box(modifier = Modifier.height(15.dp))
            content()
        }
    }
}

@Composable
fun RemoveGlareSettingItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingItemSingleLineBackground(
        stringResource(R.string.setting_option_name_remove_glare),
        stringResource(R.string.setting_option_expl_remove_glare)
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            thumbContent = if (checked) {
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

@Composable
fun DocModeSettingItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingItemSingleLineBackground(
        stringResource(R.string.setting_option_name_doc_mode),
        stringResource(R.string.setting_option_expl_doc_mode)
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            thumbContent = if (checked) {
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

@Composable
fun ColorSensitivitySettingItem(
    value: Float,
    onValueChange: (Float) -> Unit,
    steps: Int?,
    valueRange: ClosedFloatingPointRange<Float>,
) {
    SettingItemMultipleLineBackground(
        stringResource(R.string.setting_option_name_color_sensitivity),
        stringResource(R.string.setting_option_expl_color_sensitivity)
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            steps = steps ?: 0,
            valueRange = valueRange
        )
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)) {
            Text(
                value.roundToInt().toString(),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun FilterTypeSettingItem(
    onClick: () -> Unit,
    buttonText: @Composable () -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit)
) {
    SettingItemMultipleLineBackground(
        stringResource(R.string.setting_option_name_color_sensitivity),
        stringResource(R.string.setting_option_expl_color_sensitivity)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick
        ) {
            Icon(Icons.Filled.FilterList, contentDescription = null)
            Box(modifier = Modifier.width(10.dp))
            buttonText()
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            content = content
        )
    }
}