package com.caucapstone.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.migrations.SharedPreferencesMigration
import com.caucapstone.app.SettingProto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

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

}

@InstallIn(SingletonComponent::class)
@Module
object SettingProtoModule {
    @Singleton
    @Provides
    fun provideProtoDataStore(@ApplicationContext context: Context) : DataStore<SettingProto> {
        return DataStoreFactory.create(
            serializer = SettingProtoSerializer,
            produceFile = { context.dataStoreFile("setting_data.pb") },
            corruptionHandler = null,
            migrations = ,
            scope =
            )
        )
    }
}