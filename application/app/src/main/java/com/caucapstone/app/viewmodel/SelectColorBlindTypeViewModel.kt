package com.caucapstone.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caucapstone.app.ColorBlindType
import com.caucapstone.app.SettingProto
import com.caucapstone.app.data.proto.SettingProtoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectColorBlindTypeViewModel @Inject constructor(
    private val settingProtoRepository: SettingProtoRepository
) : ViewModel() {
    private val _expanded = mutableStateOf(false)
    val settingFlow: Flow<SettingProto> = settingProtoRepository.flow
    val expanded: State<Boolean> = _expanded

    fun setColorBlindType(value: ColorBlindType) {
        viewModelScope.launch {
            settingProtoRepository.setColorBlindType(value)
        }
    }
    fun setExpanded(value: Boolean) {
        _expanded.value = value
    }
}