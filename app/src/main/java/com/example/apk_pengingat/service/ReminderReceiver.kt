package com.example.apk_pengingat.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.apk_pengingat.data.db.AppDatabase
import com.example.apk_pengingat.data.model.ReminderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("reminder_id", -1L)
        if (reminderId == -1L) return

        val typeName = intent.getStringExtra("type")

        CoroutineScope(Dispatchers.IO).launch {
            val reminder = AppDatabase.getDatabase(context).reminderDao()
                .getReminderById(reminderId)

            if (reminder == null) return@launch

            val title = NotificationHelper.buildNotificationTitle(reminder.type)
            val message = if (reminder.type == ReminderType.SERVICE) {
                val lastSvc = reminder.lastServiceDate?.toString() ?: "-"
                val lastKm = reminder.lastServiceKm?.toString() ?: "-"
                "Service terakhir: $lastSvc | $lastKm km\nJatuh tempo: ${reminder.expiryDate}"
            } else {
                NotificationHelper.buildNotificationMessage(
                    reminder.note,
                    reminder.expiryDate.toString()
                )
            }

            NotificationHelper.createNotificationChannel(context)
            NotificationHelper.showReminderNotification(context, reminderId, title, message)
        }
    }
}