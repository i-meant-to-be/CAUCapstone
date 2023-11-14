package com.caucapstone.app.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caucapstone.app.SettingProto
import com.caucapstone.app.data.proto.SettingProtoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingProtoRepository: SettingProtoRepository
) : ViewModel() {
    // Read
    val settingsFlow = settingProtoRepository.flow

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
}