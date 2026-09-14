package com.example.apk_pengingat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apk_pengingat.data.model.Reminder
import com.example.apk_pengingat.service.NotificationHelper
import com.example.apk_pengingat.ui.screen.AddReminderScreen
import com.example.apk_pengingat.ui.screen.HistoryScreen
import com.example.apk_pengingat.ui.screen.HomeScreen
import com.example.apk_pengingat.ui.theme.ApkPengingatTheme
import com.example.apk_pengingat.ui.viewmodel.ReminderViewModel

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class MainActivity : ComponentActivity() {

    private val viewModel: ReminderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)

        setContent {
            var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
            var editingReminder by remember { mutableStateOf<Reminder?>(null) }

            ApkPengingatTheme(
                darkTheme = when (themeMode) {
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                },
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    RequestNotificationPermissionOnFirstLaunch()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                themeMode = themeMode,
                                onToggleTheme = {
                                    themeMode = when (themeMode) {
                                        ThemeMode.SYSTEM -> ThemeMode.LIGHT
                                        ThemeMode.LIGHT -> ThemeMode.DARK
                                        ThemeMode.DARK -> ThemeMode.SYSTEM
                                    }
                                },
                                onAddReminder = { navController.navigate("add") },
                                onEditReminder = { reminder ->
                                    editingReminder = reminder
                                    navController.navigate("edit")
                                },
                                onOpenHistory = { navController.navigate("history") }
                            )
                        }
                        composable("add") {
                            AddReminderScreen(
                                viewModel = viewModel,
                                existingReminder = null,
                                onSave = { navController.popBackStack() },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("edit") {
                            AddReminderScreen(
                                viewModel = viewModel,
                                existingReminder = editingReminder,
                                onSave = { navController.popBackStack() },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("history") {
                            HistoryScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestNotificationPermissionOnFirstLaunch() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val granted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    val launcher = remember {
        androidx.activity.compose.rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }
    }

    LaunchedEffect(granted) {
        if (!granted) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}