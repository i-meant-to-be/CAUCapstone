package com.caucapstone.app.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
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
    private val _dialogState = mutableStateOf(false)
    private val _databaseDao = DatabaseModule
        .provideAppDatabase(application.applicationContext)
        .imageDao()
    val dialogState: MutableState<Boolean> = _dialogState

    fun setDialogState(value: Boolean) {
        _dialogState.value = value
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

}