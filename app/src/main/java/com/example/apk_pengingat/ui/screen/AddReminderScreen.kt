package com.example.apk_pengingat.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.apk_pengingat.data.model.Reminder
import com.example.apk_pengingat.data.model.ReminderType
import com.example.apk_pengingat.ui.viewmodel.ReminderViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
    var error by remember { mutableStateOf<String?>(null) }
    var datePickerVisible by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))

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

            Row {
                ReminderType.entries.forEach { typeOption ->
                    val label = when (typeOption) {
                        ReminderType.SIM -> "SIM"
                        ReminderType.PAJAK_TAHUNAN -> "Pajak Tahunan"
                        ReminderType.PAJAK_5TAHUN -> "Pajak 5 Tahun"
                        ReminderType.SERVICE -> "Service"
                    }
                    FilterChip(
                        selected = type == typeOption,
                        onClick = { type = typeOption },
                        label = { Text(label) },
                        modifier = Modifier.padding(end = 8.dp)
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
                label = { Text("Tanggal Jatuh Tempo *") },
                trailingIcon = {
                    IconButton(onClick = { datePickerVisible = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pilih tanggal")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = daysBefore,
                onValueChange = { daysBefore = it.filter { c -> c.isDigit() } },
                label = { Text("Pengingat Berapa Hari Sebelum") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

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
}

private fun convertPickerMillisToLocalDate(millis: Long): LocalDate {
    return java.time.Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}