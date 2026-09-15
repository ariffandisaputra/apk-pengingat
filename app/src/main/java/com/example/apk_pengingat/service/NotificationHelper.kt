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

    fun showReminderNotification(
        context: Context,
        reminderId: Long,
        alertIndex: Int,
        title: String,
        message: String
    ) {
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

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID_BASE + reminderId.toInt() * 10 + alertIndex, notification)
    }

    fun buildNotificationTitle(type: ReminderType, isPlateRenewal: Boolean = false): String {
        return when (type) {
            ReminderType.SIM -> "SIM akan berakhir!"
            ReminderType.PAJAK_TAHUNAN ->
                if (isPlateRenewal) "Pajak tahunan + ganti plat STNK!"
                else "Pajak tahunan jatuh tempo!"
            ReminderType.PAJAK_5TAHUN -> "Perpanjangan STNK 5 tahunan!"
            ReminderType.SERVICE -> "Waktunya service kendaraan!"
        }
    }

    fun buildNotificationMessage(
        note: String,
        expiryDate: String,
        biaya: Long = 0,
        isPlateRenewal: Boolean = false
    ): String {
        val sb = StringBuilder()
        if (note.isNotBlank()) {
            sb.append(note).append('\n')
        }
        sb.append("Jatuh tempo: ").append(expiryDate)
        if (isPlateRenewal) {
            sb.append("\nJangan lupa GANTI PLAT (STNK) + cek fisik!")
        }
        if (biaya > 0) {
            val formatted = "Rp " + java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format(biaya)
            sb.append("\nBiaya: ").append(formatted)
        }
        return sb.toString()
    }
}