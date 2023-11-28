package com.caucapstone.app.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caucapstone.app.data.room.DatabaseModule
import com.caucapstone.app.data.room.Image
import com.chaquo.python.Python
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ImageViewViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    private val _dialogState = mutableStateOf(0)
    private val _isImageDeleted = mutableStateOf(false)
    private val _bottomBarExpanded = mutableStateOf(false)
    private val _imageEditTextFieldValue = mutableStateOf("")
    private val _databaseDao = DatabaseModule
        .provideAppDatabase(application.applicationContext)
        .imageDao()

    val dialogState: State<Int> = _dialogState
    val bottomBarExpanded: State<Boolean> = _bottomBarExpanded
    val isImageDeleted: State<Boolean> = _isImageDeleted
    val imageEditTextFieldValue: State<String> = _imageEditTextFieldValue

    fun setDialogState(value: Int) {
        _dialogState.value = value
    }
    fun setImageDeleted() {
        _isImageDeleted.value = true
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
        originFilePath: String
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
                byteArrayOutputStream.toByteArray(),
                originImage.height,
                originImage.width,
                6, true, true
            ).asList()
        val processedImageBytes = Base64.decode(
            processedResultList[0].toString().substring(2),
            Base64.DEFAULT
        )
        val processedImage = BitmapFactory.decodeByteArray(processedImageBytes, 0, processedImageBytes.size)
        processedImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

        _dialogState.value = 0
        fileOutputStream.close()
        byteArrayOutputStream.close()
        return imageId
    }
}