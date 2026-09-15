package com.example.apk_pengingat

import androidx.test.core.app.ApplicationProvider
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.example.apk_pengingat.data.model.Reminder
import com.example.apk_pengingat.data.model.ReminderType
import com.example.apk_pengingat.data.repository.ReminderRepository
import com.example.apk_pengingat.ui.screen.AddReminderScreen
import com.example.apk_pengingat.ui.screen.HomeScreenContent
import com.example.apk_pengingat.ui.theme.ApkPengingatTheme
import com.example.apk_pengingat.ui.viewmodel.ReminderViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

private class FakeRepository(private val active: List<Reminder> = emptyList()) : ReminderRepository {
    override val activeReminders: Flow<List<Reminder>> = MutableStateFlow(active)
    override val completedReminders: Flow<List<Reminder>> = MutableStateFlow(emptyList())
    override suspend fun insert(reminder: Reminder): Long = 1L
    override suspend fun update(reminder: Reminder) {}
    override suspend fun delete(reminder: Reminder) {}
    override suspend fun markCompleted(id: Long) {}
    override suspend fun getById(id: Long): Reminder? = null
}

private fun sampleReminder(
    id: Long,
    type: ReminderType,
    title: String,
    expiry: String,
    note: String = "",
    biaya: Long = 0,
    lastServiceDate: LocalDate? = null,
    lastServiceKm: Int? = null,
    detailNote: String = ""
) = Reminder(
    id = id,
    type = type,
    title = title,
    note = note,
    expiryDate = LocalDate.parse(expiry),
    reminderDaysBefore = 30,
    lastServiceDate = lastServiceDate,
    lastServiceKm = lastServiceKm,
    biaya = biaya,
    detailNote = detailNote
)

@RunWith(JUnit4::class)
class PaparazziPreview {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_2,
        maxPercentDifference = 0.0
    )

    private val app: android.app.Application by lazy {
        ApplicationProvider.getApplicationContext()
    }

    private val sampleData = listOf(
        sampleReminder(
            id = 1, type = ReminderType.SIM, title = "SIM C",
            expiry = "2026-11-01", note = "SIM C", biaya = 300000
        ),
        sampleReminder(
            id = 2, type = ReminderType.PAJAK_TAHUNAN, title = "Pajak Tahunan Motor",
            expiry = "2026-10-20", note = "B 1234 X", biaya = 750000
        ),
        sampleReminder(
            id = 3, type = ReminderType.SERVICE, title = "Service Rutin",
            expiry = "2026-12-01", note = "B 9999 Y", biaya = 250000,
            lastServiceDate = LocalDate.parse("2026-06-01"),
            lastServiceKm = 45000,
            detailNote = "ganti oli filter tune up"
        )
    )

    @Test
    fun homeEmpty() {
        paparazzi.snapshot("home_empty") {
            ApkPengingatTheme(darkTheme = false, dynamicColor = false) {
                HomeScreenContent(
                    reminders = emptyList(),
                    themeMode = ThemeMode.LIGHT,
                    onToggleTheme = {},
                    onAddReminder = {},
                    onEditReminder = {},
                    onOpenHistory = {},
                    onComplete = {}
                )
            }
        }
    }

    @Test
    fun homePopulated() {
        paparazzi.snapshot("home_populated") {
            ApkPengingatTheme(darkTheme = false, dynamicColor = false) {
                HomeScreenContent(
                    reminders = sampleData,
                    themeMode = ThemeMode.LIGHT,
                    onToggleTheme = {},
                    onAddReminder = {},
                    onEditReminder = {},
                    onOpenHistory = {},
                    onComplete = {}
                )
            }
        }
    }

    @Test
    fun addFormPajak() {
        val vm = ReminderViewModel(app, FakeRepository())
        paparazzi.snapshot("add_form_pajak") {
            ApkPengingatTheme(darkTheme = false, dynamicColor = false) {
                AddReminderScreen(
                    viewModel = vm,
                    initialType = ReminderType.PAJAK_TAHUNAN,
                    onSave = {},
                    onBack = {}
                )
            }
        }
    }

    @Test
    fun addFormSim() {
        val vm = ReminderViewModel(app, FakeRepository())
        paparazzi.snapshot("add_form_sim") {
            ApkPengingatTheme(darkTheme = false, dynamicColor = false) {
                AddReminderScreen(
                    viewModel = vm,
                    initialType = ReminderType.SIM,
                    onSave = {},
                    onBack = {}
                )
            }
        }
    }

    @Test
    fun addFormService() {
        val vm = ReminderViewModel(app, FakeRepository())
        paparazzi.snapshot("add_form_service") {
            ApkPengingatTheme(darkTheme = false, dynamicColor = false) {
                AddReminderScreen(
                    viewModel = vm,
                    initialType = ReminderType.SERVICE,
                    onSave = {},
                    onBack = {}
                )
            }
        }
    }
}