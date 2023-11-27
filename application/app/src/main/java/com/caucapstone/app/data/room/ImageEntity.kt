package com.caucapstone.app.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "image_table")
data class Image(
    @PrimaryKey
    @ColumnInfo(name = "image_id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "image_caption")
    val caption: String,

    @ColumnInfo(name = "image_local_date_time")
    val localDateTime: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "image_is_processed")
    val isProcessed: Boolean = false,

    @ColumnInfo(name = "image_origin_id")
    val originId: String?
) {
    companion object {
        fun getDefaultInstance(): Image {
            return Image("", "", LocalDateTime.now(), false, "")
        }
    }
}

