package com.caucapstone.app.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caucapstone.app.data.room.DatabaseModule
import com.caucapstone.app.data.room.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ImageViewViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    private val _dialogState = mutableStateOf(0)
    private val _isImageDeleted = mutableStateOf(false)
    private val _bottomBarExpanded = mutableStateOf(false)
    private val _databaseDao = DatabaseModule
        .provideAppDatabase(application.applicationContext)
        .imageDao()
    val dialogState: State<Int> = _dialogState
    val bottomBarExpanded: State<Boolean> = _bottomBarExpanded
    val isImageDeleted: State<Boolean> = _isImageDeleted

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
    fun updateImage(image: Image) {
        viewModelScope.launch {
            _databaseDao.update(image)
        }
    }
    fun deleteImage(id: String) {
        viewModelScope.launch {
            _databaseDao.deleteById(id)
        }
    }
    fun addImageToDatabase(
        id: String,
        caption: String,
        originId: String? = null
    ) {
        viewModelScope.launch {
            _databaseDao.insert(Image(id, caption, originId = originId))
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
}