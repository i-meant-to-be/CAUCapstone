package com.caucapstone.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.caucapstone.app.ImageProto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private val Context.imageProtoDataStore: DataStore<ImageProto> by dataStore(
    fileName = "image_data.pb",
    serializer = ImageProtoSerializer
)

class ImageProtoRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val flow: Flow<ImageProto> = context.imageProtoDataStore.data
}