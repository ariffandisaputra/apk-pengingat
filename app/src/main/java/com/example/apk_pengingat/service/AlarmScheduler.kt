package com.example.apk_pengingat.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDate
import java.time.ZoneId

object AlarmScheduler {

    private const val REQUEST_CODE_BASE = 1000

    fun scheduleReminder(
        context: Context,
        reminderId: Long,
        expiryDate: LocalDate,
        daysBefore: Int = 30
    ): Long {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val actualReminderDays = daysBefore.coerceAtLeast(0).toLong()
        val triggerDate = expiryDate.minusDays(actualReminderDays)

        val triggerTime = triggerDate
            .atTime(9, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        scheduleExactOrInexact(context, reminderId, triggerTime)

        return triggerTime
    }

    fun rescheduleForNotification(
        context: Context,
        reminderId: Long,
        expiryDate: LocalDate
    ) {
        // Set a second alarm on the exact expiry date as a fallback reminder.
        val triggerTime = expiryDate
            .atTime(9, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        scheduleExactOrInexact(context, reminderId, triggerTime)
    }

    private fun scheduleExactOrInexact(context: Context, reminderId: Long, triggerTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.apk_pengingat.REMINDER_ALARM"
            putExtra("reminder_id", reminderId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_BASE + reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.apk_pengingat.REMINDER_ALARM"
            putExtra("reminder_id", reminderId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_BASE + reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}