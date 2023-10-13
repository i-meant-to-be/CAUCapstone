package com.caucapstone.app.data

import androidx.datastore.core.DataStore
import com.caucapstone.app.ImageData
import kotlinx.coroutines.flow.Flow

class ImageDataRepository(private val imageDataRepository: DataStore<ImageData>) {
    val flow: Flow<ImageData> = imageDataRepository.data
}