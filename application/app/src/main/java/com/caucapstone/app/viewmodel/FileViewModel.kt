package com.caucapstone.app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.caucapstone.app.data.room.DatabaseModule
import com.caucapstone.app.data.room.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    private val _databaseDao = DatabaseModule
        .provideAppDatabase(application.applicationContext)
        .imageDao()

    fun getImages(): List<Image> {
        return _databaseDao.getImages()
    }
}