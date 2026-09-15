package com.example.apk_pengingat.data.repository

import com.example.apk_pengingat.data.db.ReminderDao
import com.example.apk_pengingat.data.model.Reminder
import kotlinx.coroutines.flow.Flow

class RoomReminderRepository(private val reminderDao: ReminderDao) : ReminderRepository {

    override val activeReminders: Flow<List<Reminder>> = reminderDao.getActiveReminders()
    override val completedReminders: Flow<List<Reminder>> = reminderDao.getCompletedReminders()

    override suspend fun insert(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }

    override suspend fun update(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    override suspend fun delete(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    override suspend fun markCompleted(id: Long) {
        reminderDao.markCompleted(id)
    }

    override suspend fun getById(id: Long): Reminder? {
        return reminderDao.getReminderById(id)
    }
}