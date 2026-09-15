package com.example.apk_pengingat.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.apk_pengingat.data.model.Reminder
import com.example.apk_pengingat.data.model.ReminderType
import com.example.apk_pengingat.data.model.description
import com.example.apk_pengingat.data.model.label
import com.example.apk_pengingat.ui.viewmodel.ReminderViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddReminderScreen(
    viewModel: ReminderViewModel,
    existingReminder: Reminder? = null,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    var type by remember { mutableStateOf(existingReminder?.type ?: ReminderType.PAJAK_TAHUNAN) }
    var title by remember { mutableStateOf(existingReminder?.title.orEmpty()) }
    var note by remember { mutableStateOf(existingReminder?.note.orEmpty()) }
    var selectedDate by remember {
        mutableStateOf(existingReminder?.expiryDate ?: LocalDate.now().plusMonths(1))
    }
    var daysBefore by remember { mutableStateOf(existingReminder?.reminderDaysBefore?.toString() ?: "30") }
    var serviceDate by remember { mutableStateOf(existingReminder?.lastServiceDate) }
    var serviceKm by remember { mutableStateOf(existingReminder?.lastServiceKm?.toString().orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }
    var datePickerVisible by remember { mutableStateOf(false) }
    var serviceDatePickerVisible by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))
    val expiryLabel = if (type == ReminderType.SERVICE) "Tanggal Service Berikutnya *" else "Tanggal Jatuh Tempo *"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingReminder != null) "Edit Pengingat" else "Tambah Pengingat") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Batal") }
                },
                actions = {
                    TextButton(onClick = {
                        when {
                            title.isBlank() -> error = "Judul wajib diisi"
                            else -> {
                                val reminder = Reminder(
                                    id = existingReminder?.id ?: 0,
                                    type = type,
                                    title = title.trim(),
                                    note = note.trim(),
                                    expiryDate = selectedDate,
                                    reminderDaysBefore = daysBefore.toIntOrNull() ?: 30,
                                    lastServiceDate = if (type == ReminderType.SERVICE) serviceDate else null,
                                    lastServiceKm = if (type == ReminderType.SERVICE) serviceKm.toIntOrNull() else null,
                                    isCompleted = existingReminder?.isCompleted ?: false,
                                    createdAt = existingReminder?.createdAt ?: LocalDate.now()
                                )
                                if (existingReminder == null) {
                                    viewModel.addReminder(reminder) { onSave() }
                                } else {
                                    viewModel.updateReminder(reminder) { onSave() }
                                }
                            }
                        }
                    }) { Text("Simpan") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text("Jenis Pengingat", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow {
                ReminderType.entries.forEach { typeOption ->
                    val label = typeOption.label
                    val icon = when (typeOption) {
                        ReminderType.SIM -> Icons.Default.Badge
                        ReminderType.PAJAK_TAHUNAN -> Icons.Default.Payments
                        ReminderType.PAJAK_5TAHUN -> Icons.Default.Description
                        ReminderType.SERVICE -> Icons.Default.Build
                    }
                    FilterChip(
                        selected = type == typeOption,
                        onClick = { type = typeOption },
                        label = { Text(label) },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.padding(end = 8.dp, bottom = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = type.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul *") },
                placeholder = {
                    Text(
                        when (type) {
                            ReminderType.SIM -> "Contoh: SIM C - Budi"
                            ReminderType.PAJAK_TAHUNAN -> "Contoh: Pajak Tahunan Motor B 1234 X"
                            ReminderType.PAJAK_5TAHUN -> "Contoh: Pajak 5 Tahunan Motor B 1234 X"
                            ReminderType.SERVICE -> "Contoh: Service Rutin 10.000 km"
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan (plat nomor, dll)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = dateFormatter.format(selectedDate),
                onValueChange = {},
                readOnly = true,
                label = { Text(expiryLabel) },
                trailingIcon = {
                    IconButton(onClick = { datePickerVisible = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pilih tanggal")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (type == ReminderType.SERVICE) {
                OutlinedTextField(
                    value = serviceDate?.let { dateFormatter.format(it) }.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tanggal Service Terakhir (opsional)") },
                    trailingIcon = {
                        IconButton(onClick = { serviceDatePickerVisible = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pilih tanggal service terakhir")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = serviceKm,
                    onValueChange = { serviceKm = it.filter { c -> c.isDigit() } },
                    label = { Text("KM Terakhir (opsional)") },
                    placeholder = { Text("Contoh: 45000") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                val nextSvcDate = serviceDate?.plusMonths(6)
                val nextSvcKm = serviceKm.toIntOrNull()?.plus(10000)
                if (nextSvcDate != null || nextSvcKm != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildString {
                            append("Saran service berikutnya: ")
                            val parts = mutableListOf<String>()
                            nextSvcDate?.let { parts.add(dateFormatter.format(it)) }
                            nextSvcKm?.let { parts.add("$it km") }
                            append(parts.joinToString(" atau "))
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = daysBefore,
                onValueChange = { daysBefore = it.filter { c -> c.isDigit() } },
                label = { Text("Pengingat Pertama, Berapa Hari Sebelum") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(4.dp))

            val primaryDays = daysBefore.toIntOrNull()?.coerceAtLeast(0) ?: 30
            val alertOffsets = listOf(primaryDays, (primaryDays / 2).coerceAtLeast(0), 1)
                .distinct()
                .sortedDescending()
            val alertPreview = alertOffsets.joinToString(", ") { "$it hari" }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Diingatkan 3x: $alertPreview sebelum jatuh tempo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (existingReminder != null) {
                OutlinedButton(
                    onClick = {
                        viewModel.deleteReminder(existingReminder)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus Pengingat")
                }
            }
        }
    }

    if (datePickerVisible) {
        val initialMillis = selectedDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )

        DatePickerDialog(
            onDismissRequest = { datePickerVisible = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        selectedDate = convertPickerMillisToLocalDate(millis) ?: selectedDate
                    }
                    datePickerVisible = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { datePickerVisible = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (serviceDatePickerVisible) {
        val initialMillis = (serviceDate ?: LocalDate.now())
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )

        DatePickerDialog(
            onDismissRequest = { serviceDatePickerVisible = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val picked = convertPickerMillisToLocalDate(millis)
                        serviceDate = picked
                        if (existingReminder == null && selectedDate == LocalDate.now().plusMonths(1)) {
                            selectedDate = picked.plusMonths(6)
                        }
                    }
                    serviceDatePickerVisible = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { serviceDatePickerVisible = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

private fun convertPickerMillisToLocalDate(millis: Long): LocalDate {
    return java.time.Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}