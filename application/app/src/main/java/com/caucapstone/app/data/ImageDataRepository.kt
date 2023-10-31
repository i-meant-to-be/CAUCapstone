package com.caucapstone.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.caucapstone.app.ImageData
import com.caucapstone.app.SettingData
import kotlinx.coroutines.flow.Flow

private val Context.dataStore: DataStore<ImageData> by dataStore(
    fileName = "image_data.pb",
    serializer = ImageDataSerializer
)

class ImageDataRepository(private val imageDataRepository: DataStore<ImageData>) {
    val flow: Flow<ImageData> = imageDataRepository.data
}