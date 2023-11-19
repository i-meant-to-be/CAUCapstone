package com.caucapstone.app.data.room

import android.net.Uri
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

class Converters{
    @TypeConverter
    fun fromDateTimeToString(value: LocalDateTime) : String {
        return value.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
    }

    @TypeConverter
    fun fromStringToDateTime(value: String): LocalDateTime {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
    }

    @TypeConverter
    fun fromUriToString(value: Uri) : String {
        return value.toString()
    }

    @TypeConverter
    fun fromStringToUri(value: String) : Uri {
        return Uri.parse(value)
    }
}