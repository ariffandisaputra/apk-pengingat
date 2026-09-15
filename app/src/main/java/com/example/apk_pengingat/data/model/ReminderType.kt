package com.example.apk_pengingat.data.model

enum class ReminderType {
    SIM,
    PAJAK_TAHUNAN,
    PAJAK_5TAHUN,
    SERVICE
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
            "Pajak kendaraan tahunan (PKB) dibayar setiap tahun. Catatan: di tahun ke-5 waktunya perpanjangan STNK / pajak 5 tahunan — biaya berbeda, jangan sampai terlewat!"
        ReminderType.PAJAK_5TAHUN ->
            "Perpanjangan STNK 5 tahunan: ganti plat nomor + cek fisik kendaraan, dibayar di tahun ke-5 (menggabung dengan PKB tahunan)."
        ReminderType.SERVICE ->
            "Service rutin kendaraan. Catat tanggal & KM terakhir, aplikasi mengingatkan saat service berikutnya."
    }
