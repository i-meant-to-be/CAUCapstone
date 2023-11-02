package com.caucapstone.app.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.caucapstone.app.ImageProto
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ImageProtoSerializer : Serializer<ImageProto> {
    override val defaultValue: ImageProto
        get() = ImageProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ImageProto {
        try {
            return ImageProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ImageProto, output: OutputStream) {
        t.writeTo(output)
    }
}