package com.example.apk_pengingat.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.apk_pengingat.MainActivity
import com.example.apk_pengingat.R
import com.example.apk_pengingat.data.model.ReminderType

object NotificationHelper {

    const val CHANNEL_ID = "reminder_channel"
    const val NOTIFICATION_ID_BASE = 2000

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pengingat Jadwal",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifikasi pengingat jadwal SIM, pajak, dan service kendaraan"
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showReminderNotification(context: Context, reminderId: Long, title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            if (permission != PackageManager.PERMISSION_GRANTED) return
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BASE + reminderId.toInt(), notification)
    }

    fun buildNotificationTitle(type: ReminderType): String {
        return when (type) {
            ReminderType.SIM -> "SIM akan berakhir!"
            ReminderType.PAJAK_TAHUNAN -> "Pajak tahunan jatuh tempo!"
            ReminderType.PAJAK_5TAHUN -> "Pajak 5 tahunan akan jatuh tempo!"
            ReminderType.SERVICE -> "Waktunya service kendaraan!"
        }
    }

    fun buildNotificationMessage(note: String, expiryDate: String): String {
        return if (note.isNotBlank()) {
            "$note\nJatuh tempo: $expiryDate"
        } else {
            "Jadwal jatuh tempo: $expiryDate"
        }
    }
}