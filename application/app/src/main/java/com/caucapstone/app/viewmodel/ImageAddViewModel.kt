package com.caucapstone.app.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caucapstone.app.data.room.DatabaseModule
import com.caucapstone.app.data.room.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import java.util.UUID
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

    fun getUUID(): UUID {
        val isExists = MutableLiveData<Boolean>(true)
        var uuid = UUID.randomUUID()

        viewModelScope.launch {
            while (true) {
                val queryResult = _databaseDao.isUUIDExists(uuid)
                if (queryResult.count() == 0) {
                    isExists.value = false
                    break
                } else {
                    uuid = UUID.randomUUID()
                    continue
                }
            }
        }

        return uuid
    }
    fun setCaption(value: String) {
        _caption.value = value
    }
    fun addImageToDatabase(
        id: UUID,
        caption: String
    ) {
        viewModelScope.launch {
            _databaseDao.insert(Image(id, caption))
        }
    }
}