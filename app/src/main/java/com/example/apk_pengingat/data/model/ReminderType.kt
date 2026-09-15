package com.example.apk_pengingat.data.model

import java.util.Locale

enum class ReminderType {
    SIM,
    PAJAK_TAHUNAN,
    PAJAK_5TAHUN,
    SERVICE
}

val ReminderType.typeOrder: Int
    get() = when (this) {
        ReminderType.SIM -> 0
        ReminderType.PAJAK_TAHUNAN -> 1
        ReminderType.PAJAK_5TAHUN -> 1
        ReminderType.SERVICE -> 2
    }

fun String.toTitleCase(): String =
    split(" ").joinToString(" ") { word ->
        if (word.isEmpty()) word
        else word.replaceFirstChar { ch -> ch.titlecase(Locale.getDefault()) }
    }

val ReminderType.label: String
    get() = when (this) {
        ReminderType.SIM -> "SIM"
        ReminderType.PAJAK_TAHUNAN -> "Pajak Tahunan"
        ReminderType.PAJAK_5TAHUN -> "Pajak 5 Tahun"
        ReminderType.SERVICE -> "Service"
    }

val ReminderType.description: String
    get() = when (this) {
        ReminderType.SIM ->
            "SIM berlaku umumnya 5 tahun dan diperpanjang secara berkala. Siapkan biaya + cek kesehatan saat perpanjangan."
        ReminderType.PAJAK_TAHUNAN ->
            "Pajak kendaraan tahunan (PKB) dibayar setiap tahun. Centang 'ganti plat' jika ini tahun ke-5 untuk perpanjangan STNK — akan ada notifikasi khusus."
        ReminderType.PAJAK_5TAHUN ->
            "Perpanjangan STNK 5 tahunan: ganti plat nomor + cek fisik kendaraan, dibayar di tahun ke-5 (menggabung dengan PKB tahunan)."
        ReminderType.SERVICE ->
            "Service rutin kendaraan. Catat tanggal & KM terakhir, aplikasi mengingatkan saat service berikutnya."
    }
