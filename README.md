# Pengingat Kendaraan (ApkPengingat)

Aplikasi Android untuk mengingat jadwal perpanjangan SIM, pajak tahunan, pajak 5 tahunan, dan service kendaraan.

## Fitur

- **Pengingat SIM** - Notifikasi saat SIM akan berakhir
- **Pajak Tahunan & 5 Tahunan** - Pengingat jatuh tempo pajak kendaraan
- **Service Kendaraan** - Jadwal service berkala
- **Notifikasi & Alarm** - Notifikasi secara otomatis sebelum tanggal jatuh tempo
- **Riwayat Perpanjangan** - Catatan semua perpanjangan yang sudah selesai
- **Widget Home Screen** - Menampilkan 3 jadwal terdekat
- **Dark Mode** - Toggle terang/gelap/mengikuti sistem

## Tech Stack

| Komponen | Teknologi |
|---|---|
| Bahasa | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Database | Room (SQLite) |
| Notifikasi | AlarmManager + NotificationManager |
| Widget | Glance AppWidget |
| Architecture | MVVM |

## Cara Build

1. Buka project di **Android Studio** (Hedgehog atau lebih baru)
2. Tunggu Gradle sync selesai
3. **Run** (`Shift + F10`) atau build APK via:
   - Menu: `Build > Build Bundle(s) / APK(s) > Build APK(s)`
4. APK hasil build berada di `app/build/outputs/apk/debug/`

### Persyaratan
- Android Studio terbaru
- JDK 17
- Minimum SDK: API 26 (Android 8.0)
- Target SDK: API 34

## Izin yang Digunakan

- `POST_NOTIFICATIONS` - untuk menampilkan notifikasi (Android 13+)
- `SCHEDULE_EXACT_ALARM` - alarm tepat waktu (opsional, fallback ke alarm tidak persis)
- `RECEIVE_BOOT_COMPLETED` - menjadwalkan ulang alarm setelah HP restart

## Struktur Project

```
app/src/main/java/com/example/apk_pengingat/
├── data/
│   ├── db/          # Room database, DAO, converter
│   ├── model/       # Reminder & ReminderType
│   └── repository/  # ReminderRepository
├── service/         # Alarm & notifikasi
├── ui/
│   ├── components/  # ReminderCard dll
│   ├── screen/      # Home, Add/Edit, History
│   ├── theme/       # Material 3 theme
│   └── viewmodel/   # ReminderViewModel
├── widget/          # Glance home screen widget
└── MainActivity.kt  # Navigation & entry point
```

## Catatan Penting

### Izin Notifikasi (Android 13+)
Saat pertama kali membuka aplikasi, pengguna harus memberi izin notifikasi di pengaturan sistem.

### Izin Alarm Tepat Waktu (Android 12+)
Untuk alarm yang sangat tepat, pengguna perlu memberi akses "Alarm & reminders" di pengaturan sistem. Jika tidak, aplikasi otomatis memakai alarm tidak persis (selisih beberapa menit).

### Screenshot Alur
1. **Home** - daftar jadwal aktif dengan badge tipe & countdown hari
2. **Tambah/Edit** - pilih tipe, isi judul, catatan, tanggal (date picker), hari pengingat
3. **Riwayat** - jadwal yang sudah ditandai selesai