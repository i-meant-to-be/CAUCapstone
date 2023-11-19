package com.caucapstone.app.data.proto

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.caucapstone.app.FilterType
import com.caucapstone.app.SettingProto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private val Context.settingProtoDataStore: DataStore<SettingProto> by dataStore(
    fileName = "setting_data.pb",
    serializer = SettingProtoSerializer
)

class SettingProtoRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<SettingProto> = context.settingProtoDataStore
    val flow: Flow<SettingProto> = dataStore.data

    suspend fun setDocMode(value: Boolean) {
        dataStore.updateData { settingProto ->
            settingProto
                .toBuilder()
                .setDocMode(value)
                .build()
        }
    }

    suspend fun setRemoveGlare(value: Boolean) {
        dataStore.updateData { settingProto ->
            settingProto
                .toBuilder()
                .setRemoveGlare(value)
                .build()
        }
    }

    suspend fun setColorSensitivity(value: Int) {
        dataStore.updateData { settingProto ->
            settingProto
                .toBuilder()
                .setColorSensitivity(value)
                .build()
        }
    }

    suspend fun setDefaultFilterType(value: FilterType) {
        dataStore.updateData { settingProto ->
            settingProto
                .toBuilder()
                .setDefaultFilterType(value)
                .build()
        }
    }
}