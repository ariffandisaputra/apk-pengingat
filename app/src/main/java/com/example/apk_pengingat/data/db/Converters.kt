package com.example.apk_pengingat.data.db

import androidx.room.TypeConverter
import com.example.apk_pengingat.data.model.ReminderType
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromReminderType(type: ReminderType): String {
        return type.name
    }

    @TypeConverter
    fun toReminderType(typeName: String): ReminderType {
        return ReminderType.valueOf(typeName)
    }
}
