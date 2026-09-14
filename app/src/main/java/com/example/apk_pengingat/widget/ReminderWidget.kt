package com.example.apk_pengingat.widget

import android.content.Context
import android.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.Alignment
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.dp
import androidx.glance.unit.sp
import com.example.apk_pengingat.data.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ReminderWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val upcomingReminders = withContext(Dispatchers.IO) {
            AppDatabase.getDatabase(context).reminderDao()
                .getUniqueActiveReminders()
        }

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(color = ColorProvider(Color.parseColor("#1976D2")))
                    .padding(12.dp),
                verticalAlignment = Alignment.Vertical.Top
            ) {
                Text(
                    text = "Pengingat Kendaraan",
                    style = TextStyle(
                        color = ColorProvider(Color.WHITE),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Spacer(GlanceModifier.height(4.dp))

                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))
                Text(
                    text = "Hari ini: ${formatter.format(LocalDate.now())}",
                    style = TextStyle(
                        color = ColorProvider(Color.parseColor("#BBDEFB")),
                        fontSize = 12.sp
                    )
                )

                if (upcomingReminders.isEmpty()) {
                    Spacer(GlanceModifier.height(12.dp))
                    Text(
                        text = "Tidak ada jadwal aktif",
                        style = TextStyle(
                            color = ColorProvider(Color.parseColor("#E3F2FD")),
                            fontSize = 12.sp
                        )
                    )
                } else {
                    Spacer(GlanceModifier.height(8.dp))
                    upcomingReminders.forEachIndexed { index, reminder ->
                        Text(
                            text = "${index + 1}. ${reminder.title}",
                            style = TextStyle(
                                color = ColorProvider(Color.WHITE),
                                fontSize = 12.sp
                            ),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "   ${formatter.format(reminder.expiryDate)}",
                            style = TextStyle(
                                color = ColorProvider(Color.parseColor("#BBDEFB")),
                                fontSize = 11.sp
                            )
                        )
                        Spacer(GlanceModifier.height(4.dp))
                    }
                }
            }
        }
    }
}

class ReminderWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ReminderWidget()
}