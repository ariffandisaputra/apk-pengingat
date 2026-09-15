package com.example.apk_pengingat.data.repository

import com.example.apk_pengingat.data.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    val activeReminders: Flow<List<Reminder>>
    val completedReminders: Flow<List<Reminder>>

    suspend fun insert(reminder: Reminder): Long

    suspend fun update(reminder: Reminder)

    suspend fun delete(reminder: Reminder)

    suspend fun markCompleted(id: Long)

    suspend fun getById(id: Long): Reminder?
}