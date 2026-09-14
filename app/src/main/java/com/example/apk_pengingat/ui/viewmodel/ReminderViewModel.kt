package com.example.apk_pengingat.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.apk_pengingat.data.db.AppDatabase
import com.example.apk_pengingat.data.model.Reminder
import com.example.apk_pengingat.data.repository.ReminderRepository
import com.example.apk_pengingat.service.AlarmScheduler
import com.example.apk_pengingat.service.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ReminderRepository(
        AppDatabase.getDatabase(application).reminderDao()
    )

    private val appContext = application.applicationContext

    val activeReminders: StateFlow<List<Reminder>> = repository.activeReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedReminders: StateFlow<List<Reminder>> = repository.completedReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(reminder: Reminder, onInserted: (Long) -> Unit = {}) {
        viewModelScope.launch {
            NotificationHelper.createNotificationChannel(appContext)
            val id = repository.insert(reminder)
            AlarmScheduler.scheduleReminder(
                context = appContext,
                reminderId = id,
                expiryDate = reminder.expiryDate,
                daysBefore = reminder.reminderDaysBefore
            )
            onInserted(id)
        }
    }

    fun updateReminder(reminder: Reminder, onUpdated: () -> Unit = {}) {
        viewModelScope.launch {
            repository.update(reminder)
            AlarmScheduler.cancelReminder(appContext, reminder.id)
            AlarmScheduler.scheduleReminder(
                context = appContext,
                reminderId = reminder.id,
                expiryDate = reminder.expiryDate,
                daysBefore = reminder.reminderDaysBefore
            )
            onUpdated()
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            AlarmScheduler.cancelReminder(appContext, reminder.id)
            repository.delete(reminder)
        }
    }

    fun markCompleted(reminder: Reminder, onCompleted: () -> Unit = {}) {
        viewModelScope.launch {
            AlarmScheduler.cancelReminder(appContext, reminder.id)
            repository.markCompleted(reminder.id)
            onCompleted()
        }
    }

    suspend fun getReminder(id: Long): Reminder? {
        return repository.getById(id)
    }
}