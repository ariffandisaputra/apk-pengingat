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
    private const val REPEATS = 3

    fun scheduleReminder(
        context: Context,
        reminderId: Long,
        expiryDate: LocalDate,
        daysBefore: Int = 30
    ): Long {
        val primary = daysBefore.coerceAtLeast(0)
        val offsets = listOf(primary, (primary / 2).coerceAtLeast(0), 1)
            .distinct()
            .sortedDescending()

        var last = 0L
        offsets.forEachIndexed { index, offsetDays ->
            val triggerDate = expiryDate.minusDays(offsetDays.toLong())
            val triggerTime = triggerDate
                .atTime(9, 0)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            scheduleExactOrInexact(context, reminderId, index, triggerTime)
            last = triggerTime
        }
        return last
    }

    fun rescheduleForNotification(
        context: Context,
        reminderId: Long,
        expiryDate: LocalDate
    ) {
        scheduleReminder(context, reminderId, expiryDate, 0)
    }

    private fun scheduleExactOrInexact(
        context: Context,
        reminderId: Long,
        alertIndex: Int,
        triggerTime: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.apk_pengingat.REMINDER_ALARM"
            putExtra("reminder_id", reminderId)
            putExtra("alert_index", alertIndex)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode(reminderId, alertIndex),
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
        repeat(REPEATS) { alertIndex ->
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                action = "com.example.apk_pengingat.REMINDER_ALARM"
                putExtra("reminder_id", reminderId)
                putExtra("alert_index", alertIndex)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode(reminderId, alertIndex),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun requestCode(reminderId: Long, alertIndex: Int): Int {
        return REQUEST_CODE_BASE + reminderId.toInt() * 10 + alertIndex
    }
}