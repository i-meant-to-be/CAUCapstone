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
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "image_caption")
    val caption: String,

    @ColumnInfo(name = "image_local_date_time")
    val localDateTime: LocalDateTime = LocalDateTime.now()
)