package com.caucapstone.app.viewmodel

import android.app.Application
import android.net.Uri
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
class ImageAddViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    private val _caption = mutableStateOf("")
    private val _databaseDao = DatabaseModule
        .provideAppDatabase(application.applicationContext)
        .imageDao()
    val caption: State<String> = _caption

    fun setCaption(value: String) {
        _caption.value = value
    }
    fun addImageToDatabase(
        caption: String,
        uri: Uri
    ) {
        viewModelScope.launch {
            _databaseDao.insert(Image(caption = caption, uri = uri))
        }
    }
}