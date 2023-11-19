package com.caucapstone.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.caucapstone.app.FilterType
import com.caucapstone.app.SettingProto
import com.caucapstone.app.data.proto.SettingProtoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val settingProtoRepository: SettingProtoRepository
) : ViewModel() {
    private val _settingFlow = settingProtoRepository.flow
    private val _isInitiated = mutableStateOf(false)
    private val _currFilterType = mutableStateOf(FilterType.FILTER_NONE)
    val settingFlow: Flow<SettingProto> = _settingFlow
    val isInitiated: State<Boolean> = _isInitiated
    val currFilterType: State<FilterType> = _currFilterType

    fun setCurrFilterType(value: FilterType) {
        _currFilterType.value = value
    }
    fun setIsInitiated() {
        _isInitiated.value = true
    }
}