package com.caucapstone.app.data.room

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.*

@Entity(tableName = "image_table")
data class Image @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "image_caption")
    val caption: String,

    @ColumnInfo(name = "image_datetime")
    val dateTime: Date = Date.from(Instant.now())
)