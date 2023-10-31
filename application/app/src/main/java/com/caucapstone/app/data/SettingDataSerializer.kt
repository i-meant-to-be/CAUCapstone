package com.caucapstone.app.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.caucapstone.app.SettingData
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SettingDataSerializer : Serializer<SettingData> {
    override val defaultValue: SettingData
        get() = SettingData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SettingData {
        try {
            return SettingData.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: SettingData, output: OutputStream) {
        t.writeTo(output)
    }
}