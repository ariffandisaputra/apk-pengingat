package com.example.apk_pengingat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.apk_pengingat.data.model.Reminder
import com.example.apk_pengingat.data.model.ReminderType
import com.example.apk_pengingat.ui.theme.Amber500
import com.example.apk_pengingat.ui.theme.Blue400
import com.example.apk_pengingat.ui.theme.Green400
import com.example.apk_pengingat.ui.theme.Orange500
import com.example.apk_pengingat.ui.theme.Red400
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderCard(
    reminder: Reminder,
    onComplete: () -> Unit,
    onClick: () -> Unit
) {
    val daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), reminder.expiryDate)

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TypeBadge(reminder.type)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (reminder.note.isNotBlank()) {
                    Text(
                        text = reminder.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (reminder.type == ReminderType.SERVICE && reminder.detailNote.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Catatan: ${reminder.detailNote}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (reminder.type == ReminderType.SERVICE &&
                    (reminder.lastServiceDate != null || reminder.lastServiceKm != null)
                ) {
                    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))
                    val lastDate = reminder.lastServiceDate?.format(formatter) ?: "-"
                    val lastKm = reminder.lastServiceKm?.toString() ?: "-"
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Service: $lastDate | $lastKm km",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (reminder.type == ReminderType.PAJAK_TAHUNAN && reminder.isPlateRenewal) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Orange500.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Ganti Plat",
                            style = MaterialTheme.typography.labelSmall,
                            color = Orange500,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                if (reminder.biaya > 0) {
                    val biayaText = "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(reminder.biaya)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Biaya: $biayaText",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                DaysLeftText(daysLeft)
            }
            IconButton(onClick = onComplete) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Tandai selesai",
                    tint = Green400
                )
            }
        }
    }
}

@Composable
fun TypeBadge(type: ReminderType) {
    val (label, color) = when (type) {
        ReminderType.SIM -> "SIM" to Blue400
        ReminderType.PAJAK_TAHUNAN -> "Pajak Tahunan" to Amber500
        ReminderType.PAJAK_5TAHUN -> "Pajak 5 Tahun" to Orange500
        ReminderType.SERVICE -> "Service" to Red400
    }
    val icon = when (type) {
        ReminderType.SIM -> Icons.Default.Badge
        ReminderType.PAJAK_TAHUNAN -> Icons.Default.Payments
        ReminderType.PAJAK_5TAHUN -> Icons.Default.Description
        ReminderType.SERVICE -> Icons.Default.Build
    }

    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun DaysLeftText(daysLeft: Long) {
    val (text, color) = when {
        daysLeft < 0 -> "Lewat ${-daysLeft} hari" to Red400
        daysLeft == 0L -> "Hari ini jatuh tempo!" to Red400
        daysLeft <= 7 -> "Tinggal $daysLeft hari" to Orange500
        daysLeft <= 30 -> "Tinggal $daysLeft hari" to Amber500
        else -> "Tinggal $daysLeft hari" to Green400
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Bold
    )
}