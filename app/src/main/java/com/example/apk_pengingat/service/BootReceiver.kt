package com.example.apk_pengingat.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.apk_pengingat.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(context).reminderDao()
            val reminders = dao.getActiveReminders().first()
            reminders.forEach { reminder ->
                if (!reminder.isCompleted) {
                    AlarmScheduler.scheduleReminder(
                        context = context,
                        reminderId = reminder.id,
                        expiryDate = reminder.expiryDate,
                        daysBefore = reminder.reminderDaysBefore
                    )
                }
            }
        }
    }
}