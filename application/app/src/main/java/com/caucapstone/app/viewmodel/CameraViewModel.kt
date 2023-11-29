package com.caucapstone.app.viewmodel

import android.app.Application
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caucapstone.app.FilterType
import com.caucapstone.app.SettingProto
import com.caucapstone.app.data.proto.SettingProtoRepository
import com.caucapstone.app.data.room.DatabaseModule
import com.caucapstone.app.data.room.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    application: Application,
    private val settingProtoRepository: SettingProtoRepository
) : ViewModel() {
    private val _snackbarHostState = SnackbarHostState()
    private val _databaseDao = DatabaseModule
        .provideAppDatabase(application.applicationContext)
        .imageDao()
    private val _settingFlow = settingProtoRepository.flow
    private val _currFilterType = mutableStateOf(FilterType.FILTER_NONE)
    private val _colorCodes = mutableStateOf(Triple(0, 0, 0))
    private val _aprxColorCodes = mutableStateOf(Triple(0, 0, 0))
    private val _aprxColorName = mutableStateOf("")
    private val _sliderValue = mutableFloatStateOf(0f)
    private val _imageCaptureUseCase = mutableStateOf(
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    )
    private val _imageAnalyzeFilters = mutableStateOf(
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
    )

    val aprxColorCodes: State<Triple<Int, Int, Int>> = _aprxColorCodes
    val colorCodes: State<Triple<Int, Int, Int>> = _colorCodes
    val aprxColorNames: State<String> = _aprxColorName
    val snackbarHostState: SnackbarHostState = _snackbarHostState
    val settingFlow: Flow<SettingProto> = _settingFlow
    val currFilterType: State<FilterType> = _currFilterType
    val sliderValue: State<Float> = _sliderValue
    val imageCaptureUseCase: State<ImageCapture> = _imageCaptureUseCase
    val imageAnalyzeFilters: State<ImageAnalysis> = _imageAnalyzeFilters

    fun setCurrFilterType(value: FilterType) {
        _currFilterType.value = value
    }
    fun setColorCodes(value: Triple<Int, Int, Int>) {
        _colorCodes.value = value
    }
    fun setAprxColorCodes(value: Triple<Int, Int, Int>) {
        _aprxColorCodes.value = value
    }
    fun setAprxColorName(value: String) {
        _aprxColorName.value = value
    }
    fun setSliderValue(value: Float) {
        _sliderValue.floatValue = value
    }
    fun getUUID(): String {
        val isExists = MutableLiveData<Boolean>(true)
        var uuid = UUID.randomUUID()

        viewModelScope.launch {
            while (true) {
                val queryResult = _databaseDao.isUUIDExists(uuid.toString())
                if (queryResult.isEmpty()) {
                    isExists.value = false
                    break
                } else {
                    uuid = UUID.randomUUID()
                    continue
                }
            }
        }

        return uuid.toString()
    }
    fun addImageToDatabase(
        id: String,
        caption: String
    ) {
        viewModelScope.launch {
            _databaseDao.insert(Image(id, caption, originId = null))
        }
    }
}