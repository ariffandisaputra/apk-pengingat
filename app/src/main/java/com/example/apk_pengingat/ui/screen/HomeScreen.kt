package com.example.apk_pengingat.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.apk_pengingat.ThemeMode
import com.example.apk_pengingat.data.model.Reminder
import com.example.apk_pengingat.data.model.ReminderType
import com.example.apk_pengingat.ui.components.ReminderCard
import com.example.apk_pengingat.ui.theme.Amber500
import com.example.apk_pengingat.ui.theme.Blue400
import com.example.apk_pengingat.ui.theme.Red400
import com.example.apk_pengingat.ui.viewmodel.ReminderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ReminderViewModel,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    onAddReminder: () -> Unit,
    onEditReminder: (Reminder) -> Unit,
    onOpenHistory: () -> Unit
) {
    val reminders by viewModel.activeReminders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengingat Kendaraan") },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        val icon = when (themeMode) {
                            ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                            ThemeMode.LIGHT -> Icons.Default.LightMode
                            ThemeMode.DARK -> Icons.Default.DarkMode
                        }
                        Icon(icon, contentDescription = "Ganti tema")
                    }
                    IconButton(onClick = onOpenHistory) {
                        Icon(Icons.Default.History, contentDescription = "Riwayat")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddReminder) {
                Icon(Icons.Default.Add, contentDescription = "Tambah pengingat")
            }
        }
    ) { padding ->
        if (reminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Belum ada pengingat",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tekan tombol + untuk menambah jadwal\nSIM, pajak, atau service kendaraan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            val sections = buildList<Pair<ReminderType, List<Reminder>>> {
                listOf(ReminderType.SIM, ReminderType.PAJAK_TAHUNAN, ReminderType.SERVICE).forEach { sectionType ->
                    val group = reminders
                        .filter {
                            it.type == sectionType ||
                                (sectionType == ReminderType.PAJAK_TAHUNAN && it.type == ReminderType.PAJAK_5TAHUN)
                        }
                        .sortedBy { it.expiryDate }
                    if (group.isNotEmpty()) add(sectionType to group)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jadwal Aktif",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${reminders.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                sections.forEach { (sectionType, group) ->
                    item(key = "header-${sectionType.name}") {
                        val (sectionLabel, sectionIcon, sectionColor) = when (sectionType) {
                            ReminderType.SIM -> Triple("SIM", Icons.Default.Badge, Blue400)
                            ReminderType.PAJAK_TAHUNAN -> Triple("Pajak Kendaraan", Icons.Default.Payments, Amber500)
                            ReminderType.PAJAK_5TAHUN -> Triple("Pajak Kendaraan", Icons.Default.Payments, Amber500)
                            ReminderType.SERVICE -> Triple("Service Kendaraan", Icons.Default.Build, Red400)
                        }
                        SectionHeader(
                            label = sectionLabel,
                            icon = sectionIcon,
                            color = sectionColor,
                            count = group.size
                        )
                    }
                    items(group, key = { it.id }) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onComplete = { viewModel.markCompleted(reminder) },
                            onClick = { onEditReminder(reminder) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    label: String,
    icon: ImageVector,
    color: Color,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Surface(
            shape = RoundedCornerShape(50),
            color = color.copy(alpha = 0.12f)
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelMedium,
                color = color,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}