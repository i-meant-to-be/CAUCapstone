package com.caucapstone.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caucapstone.app.data.room.DatabaseModule
import com.caucapstone.app.data.room.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    private val _dialogState = mutableStateOf(0)
    private val _bottomBarExpanded = mutableStateOf(false)
    private val _databaseDao = DatabaseModule
        .provideAppDatabase(application.applicationContext)
        .imageDao()
    val dialogState: State<Int> = _dialogState
    val bottomBarExpanded: State<Boolean> = _bottomBarExpanded

    fun setDialogState(value: Int) {
        _dialogState.value = value
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
        Log.e("CAUCAPSTONE", id)
        viewModelScope.launch {
            _databaseDao.deleteById(id)
        }
    }
}