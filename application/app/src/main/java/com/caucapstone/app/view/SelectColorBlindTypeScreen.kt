package com.caucapstone.app.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.caucapstone.app.ColorBlindType
import com.caucapstone.app.R
import com.caucapstone.app.SettingProto
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.viewmodel.SelectColorBlindTypeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectColorBlindTypeScreen(
    onNavigate: () -> Unit,
    viewModel: SelectColorBlindTypeViewModel = hiltViewModel()
) {
    val data = viewModel.settingFlow.collectAsState(SettingProto.getDefaultInstance()).value

    Scaffold {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = globalPaddingValue,
                    start = globalPaddingValue,
                    end = globalPaddingValue,
                    bottom = it.calculateBottomPadding()
                )
        ) {
            UniversalIndicator(
                icon = Icons.Filled.ColorLens,
                message = stringResource(R.string.string_please_select_color_blind_type)
            )
            Box(modifier = Modifier.height(30.dp))
            Button(
                onClick = { viewModel.setExpanded(true) }
            ) {
                val text = when (data.colorBlindType) {
                    ColorBlindType.COLOR_BLIND_PROTANOPIA -> stringResource(R.string.color_blind_name_protanopia)
                    ColorBlindType.COLOR_BLIND_DEUTERANOPIA -> stringResource(R.string.color_blind_name_deuteranopia)
                    ColorBlindType.COLOR_BLIND_TRITANOPIA -> stringResource(R.string.color_blind_name_tritanopia)
                    else -> stringResource(R.string.color_blind_name_none)
                }
                Icon(Icons.Filled.ColorLens, null)
                Box(modifier = Modifier.width(globalPaddingValue))
                Text(text)
            }
            DropdownMenu(
                expanded = viewModel.expanded.value,
                onDismissRequest = { viewModel.setExpanded(false) }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.color_blind_name_none)) },
                    onClick = {
                        viewModel.setColorBlindType(ColorBlindType.COLOR_BLIND_NONE)
                        viewModel.setExpanded(false)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.color_blind_name_protanopia)) },
                    onClick = {
                        viewModel.setColorBlindType(ColorBlindType.COLOR_BLIND_PROTANOPIA)
                        viewModel.setExpanded(false)
                        onNavigate()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.color_blind_name_deuteranopia)) },
                    onClick = {
                        viewModel.setColorBlindType(ColorBlindType.COLOR_BLIND_DEUTERANOPIA)
                        viewModel.setExpanded(false)
                        onNavigate()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.color_blind_name_tritanopia)) },
                    onClick = {
                        viewModel.setColorBlindType(ColorBlindType.COLOR_BLIND_TRITANOPIA)
                        viewModel.setExpanded(false)
                        onNavigate()
                    }
                )
            }
        }
    }
}