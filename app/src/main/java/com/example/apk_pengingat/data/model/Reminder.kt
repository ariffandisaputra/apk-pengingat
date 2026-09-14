package com.example.apk_pengingat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: ReminderType,
    val title: String,
    val note: String = "",
    val expiryDate: LocalDate,
    val reminderDaysBefore: Int = 30,
    val isCompleted: Boolean = false,
    val createdAt: LocalDate = LocalDate.now()
)
