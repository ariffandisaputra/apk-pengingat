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

        val alertIndex = intent.getIntExtra("alert_index", 0)
        val sequence = "Ingatan ${alertIndex + 1} dari 3"

        CoroutineScope(Dispatchers.IO).launch {
            val reminder = AppDatabase.getDatabase(context).reminderDao()
                .getReminderById(reminderId)

            if (reminder == null) return@launch

            val isPlateRenewal = reminder.type == ReminderType.PAJAK_TAHUNAN && reminder.isPlateRenewal
            val title = NotificationHelper.buildNotificationTitle(reminder.type, isPlateRenewal)
            val message = if (reminder.type == ReminderType.SERVICE) {
                val lastSvc = reminder.lastServiceDate?.toString() ?: "-"
                val lastKm = reminder.lastServiceKm?.toString() ?: "-"
                val serviceInfo = "Service terakhir: $lastSvc | $lastKm km"
                val biayaLine = if (reminder.biaya > 0) {
                    " | Biaya: Rp " + java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format(reminder.biaya)
                } else ""
                "$serviceInfo$biayaLine\nJatuh tempo: ${reminder.expiryDate}\n$sequence"
            } else {
                val base = NotificationHelper.buildNotificationMessage(
                    note = reminder.note,
                    expiryDate = reminder.expiryDate.toString(),
                    biaya = reminder.biaya,
                    isPlateRenewal = isPlateRenewal
                )
                "$base\n$sequence"
            }

            NotificationHelper.createNotificationChannel(context)
            NotificationHelper.showReminderNotification(
                context,
                reminderId,
                alertIndex,
                title,
                message
            )
        }
    }
}