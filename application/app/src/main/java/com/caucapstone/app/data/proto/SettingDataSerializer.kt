package com.caucapstone.app.data.proto

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.caucapstone.app.SettingProto
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SettingProtoSerializer : Serializer<SettingProto> {
    override val defaultValue: SettingProto
        get() = SettingProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SettingProto {
        try {
            return SettingProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: SettingProto, output: OutputStream) {
        t.writeTo(output)
    }
}