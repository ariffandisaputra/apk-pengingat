package com.example.apk_pengingat.data.repository

import com.example.apk_pengingat.data.db.ReminderDao
import com.example.apk_pengingat.data.model.Reminder
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {

    val activeReminders: Flow<List<Reminder>> = reminderDao.getActiveReminders()
    val completedReminders: Flow<List<Reminder>> = reminderDao.getCompletedReminders()

    suspend fun insert(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }

    suspend fun update(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun delete(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    suspend fun markCompleted(id: Long) {
        reminderDao.markCompleted(id)
    }

    suspend fun getById(id: Long): Reminder? {
        return reminderDao.getReminderById(id)
    }
}
