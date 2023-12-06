package com.caucapstone.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caucapstone.app.ColorBlindType
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
    private val _filterTypeExpanded = mutableStateOf(false)
    private val _colorBlindTypeExpanded = mutableStateOf(false)
    val settingFlow: Flow<SettingProto> = settingProtoRepository.flow
    val filterTypeExpanded: State<Boolean> = _filterTypeExpanded
    val colorBlindTypeExpanded: State<Boolean> = _colorBlindTypeExpanded

    fun setFilterTypeExpanded(value: Boolean) {
        _filterTypeExpanded.value = value
    }
    fun setColorBlindTypeExpanded(value: Boolean) {
        _colorBlindTypeExpanded.value = value
    }
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
    fun setDefaultFilterType(value: FilterType) {
        viewModelScope.launch {
            settingProtoRepository.setDefaultFilterType(value)
        }
    }
    fun setColorBlindType(value: ColorBlindType) {
        viewModelScope.launch {
            settingProtoRepository.setColorBlindType(value)
        }
    }
}