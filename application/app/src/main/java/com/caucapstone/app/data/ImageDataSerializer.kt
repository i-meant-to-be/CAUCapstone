package com.caucapstone.app.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.caucapstone.app.ImageData
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ImageDataSerializer : Serializer<ImageData> {
    override val defaultValue: ImageData
        get() = ImageData.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ImageData {
        try {
            return ImageData.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ImageData, output: OutputStream) {
        t.writeTo(output)
    }
}