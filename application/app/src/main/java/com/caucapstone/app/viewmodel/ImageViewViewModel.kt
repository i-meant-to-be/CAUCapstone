package com.caucapstone.app.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caucapstone.app.SettingProto
import com.caucapstone.app.data.proto.SettingProtoRepository
import com.caucapstone.app.data.room.DatabaseModule
import com.caucapstone.app.data.room.Image
import com.chaquo.python.Python
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ImageViewViewModel @Inject constructor(
    application: Application,
    private val settingRepository: SettingProtoRepository
) : ViewModel() {
    private val _settingFlow = settingRepository.flow
    private val _dialogState = mutableIntStateOf(0)
    private val _isImageDeleted = mutableStateOf(false)
    private val _bottomBarExpanded = mutableStateOf(false)
    private val _imageEditTextFieldValue = mutableStateOf("")
    private val _databaseDao = DatabaseModule
        .provideAppDatabase(application.applicationContext)
        .imageDao()
    private val _offset = mutableStateOf(Pair(0f, 0f))

    val settingFlow: Flow<SettingProto> = _settingFlow
    val dialogState: State<Int> = _dialogState
    val bottomBarExpanded: State<Boolean> = _bottomBarExpanded
    val isImageDeleted: State<Boolean> = _isImageDeleted
    val imageEditTextFieldValue: State<String> = _imageEditTextFieldValue
    val offset: State<Pair<Float, Float>> = _offset

    private fun getUUID(): String {
        val isExists = MutableLiveData(true)
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
    fun setDialogState(value: Int) {
        _dialogState.intValue = value
    }
    fun setOffset(value: Pair<Float, Float>) {
        _offset.value = value
    }
    fun setOffset(a: Float, b: Float) {
        _offset.value = Pair(a, b)
    }
    fun reverseBottomBarExpanded() {
        _bottomBarExpanded.value = !_bottomBarExpanded.value
    }
    fun getImageById(id: String): Image {
        return _databaseDao.getImageById(id)
    }
    fun setImageEditTextFieldValue(value: String) {
        _imageEditTextFieldValue.value = value
    }
    fun updateImage(image: Image) {
        viewModelScope.launch {
            _databaseDao.update(image)
        }
    }
    fun deleteImageOnDatabase(id: String) {
        viewModelScope.launch {
            _databaseDao.deleteById(id)
        }
    }
    fun deleteImageOnStorage(path: String) {
        val imageFile = File(path)
        if (imageFile.exists()) {
            imageFile.delete()
            _isImageDeleted.value = true
        }
    }
    fun addImageToDatabase(image: Image) {
        viewModelScope.launch {
            _databaseDao.insert(image)
        }
    }
    fun shareImage(
        context: Context,
        originFilePath: String
    ) {
        val imageUri = FileProvider.getUriForFile(
            context,
            "com.caucapstone.app.provider",
            File(originFilePath)
        )
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/png"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent, null)
    }
    fun processImage(
        context: Context,
        originFilePath: String,
        docMode: Boolean,
        removeGlare: Boolean,
        colorSensitivity: Int
    ): String {
        val py = Python.getInstance()
        val module = py.getModule("image_process")
        val imageId = getUUID()
        val fileOutputStream = context.openFileOutput("${imageId}.png", Context.MODE_PRIVATE)
        val byteArrayOutputStream = ByteArrayOutputStream()
        val originImage = BitmapFactory.decodeStream(FileInputStream(File(originFilePath)))

        originImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val processedResultList = module
            .callAttr(
            "process_image",
                /* value */ byteArrayOutputStream.toByteArray(),
                /* height */ originImage.height,
                /* width */ originImage.width,
                /* colorSensitivity */ colorSensitivity,
                /* docMode */ docMode,
                /* glare */ removeGlare
            ).asList()
        val processedImageBytes = Base64.decode(
            processedResultList[0].toString().substring(2),
            Base64.DEFAULT
        )
        val processedImage = BitmapFactory.decodeByteArray(processedImageBytes, 0, processedImageBytes.size)
        processedImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

        _dialogState.intValue = 0
        fileOutputStream.close()
        byteArrayOutputStream.close()
        return imageId
    }
}