package com.caucapstone.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.caucapstone.app.SettingData
import kotlinx.coroutines.flow.Flow

private val Context.dataStore: DataStore<SettingData> by dataStore(
    fileName = "setting_data.pb",
    serializer = SettingDataSerializer
)

class SettingDataRepository(private val settingDataRepository: DataStore<SettingData>) {
    val flow: Flow<SettingData> = settingDataRepository.data
}