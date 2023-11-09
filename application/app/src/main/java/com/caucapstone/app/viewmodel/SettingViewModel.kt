package com.caucapstone.app.viewmodel

import androidx.lifecycle.ViewModel
import com.caucapstone.app.data.proto.SettingProtoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingProtoRepository: SettingProtoRepository
) : ViewModel() {
    val flow = settingProtoRepository.flow
}