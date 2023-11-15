package com.caucapstone.app.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caucapstone.app.FilterType
import com.caucapstone.app.SettingProto
import com.caucapstone.app.data.proto.SettingProtoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingProtoRepository: SettingProtoRepository
) : ViewModel() {
    private val _settingsFlow = settingProtoRepository.flow
    private val _filterTypeExpanded = mutableStateOf(false)
    val settingsFlow: Flow<SettingProto> = _settingsFlow
    val filterTypeExpanded: MutableState<Boolean> = _filterTypeExpanded

    // Write
    fun setDocMode(value: Boolean) {
        viewModelScope.launch {
            settingProtoRepository.setDocMode(value)
        }
    }
    fun setRemoveGlare(value: Boolean) {
        viewModelScope.launch {
            settingProtoRepository.setRemoveGlare(value)
        }
    }
    fun setColorSensitivity(value: Int) {
        viewModelScope.launch {
            if (value >= -5 && value <= 5) settingProtoRepository.setColorSensitivity(value)
        }
    }
    fun setFilterMode(value: FilterType) {
        viewModelScope.launch {
            settingProtoRepository.setFilterType(value)
        }
    }
}