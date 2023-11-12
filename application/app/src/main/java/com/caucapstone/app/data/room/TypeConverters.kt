package com.caucapstone.app.data.room

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

class Converters{
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromDateTimeToString(value: LocalDateTime) : String {
        return value.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromStringToDateTime(value: String): LocalDateTime {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
    }
}