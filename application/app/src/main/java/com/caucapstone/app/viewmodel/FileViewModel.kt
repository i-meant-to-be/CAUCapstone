package com.caucapstone.app.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caucapstone.app.data.room.DatabaseModule
import com.caucapstone.app.data.room.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    private val _databaseDao = DatabaseModule
        .provideAppDatabase(application.applicationContext)
        .imageDao()
    private val _dialogState = mutableIntStateOf(0)
    private val _longPressEnabled = mutableStateOf(false)
    private val _recomposeSwitch = mutableStateOf(false)

    val imageIdToDelete = mutableListOf<String>()
    val dialogState: State<Int> = _dialogState
    val longPressEnabled: State<Boolean> = _longPressEnabled
    val recomposeSwitch: State<Boolean> = _recomposeSwitch

    fun reverseRecomposeSwitch() {
        _recomposeSwitch.value = !_recomposeSwitch.value
    }
    fun getImages(): List<Image> {
        return _databaseDao.getImages()
    }
    fun setDialogState(value: Int) {
        _dialogState.intValue = value
    }
    fun setLongPressEnabled(value: Boolean) {
        _longPressEnabled.value = value
    }
    fun deleteImageOnStorage(path: String) {
        val imageFile = File(path)
        if (imageFile.exists()) {
            imageFile.delete()
        }
    }
    fun deleteSelectedImageOnDatabase() {
        viewModelScope.launch {
            imageIdToDelete.forEach { imageId ->
                _databaseDao.deleteById(imageId)
            }
        }
    }
    fun deleteAllImage() {
        viewModelScope.launch {
            _databaseDao.deleteAll()
        }
    }
}