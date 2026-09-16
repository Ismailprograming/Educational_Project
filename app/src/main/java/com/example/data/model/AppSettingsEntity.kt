package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: Long = 1L,
    val dailyReportFormat: String = "PDF", // "PDF" or "IMAGE"
    val monthlyReportFormat: String = "PDF",
    val autoMonthlyReport: Boolean = true,
    val monthlyScheduleDay: Int = 28,
    val autoAbsentAlert: Boolean = true,
    val dailyReminderTime: String = "17:00",
    val offlineSyncEnabled: Boolean = true,
    val whatsappTemplate: String = """
السلام علیکم ورحمۃ اللہ وبرکاتہ

محترم والدین،

آپ کے بچے {StudentName} کی آج کی حفظ القرآن رپورٹ پیش خدمت ہے۔

سبق:
{Sabaq}

سبقی:
{Sabqi}

منزل:
{Manzil}

سبقی کی غلطیاں:
{SabqiMistakes}

منزل کی غلطیاں:
{ManzilMistakes}

حاضری:
{Attendance}

کارکردگی:
{Performance}

اللہ تعالیٰ آپ کے بچے کو قرآن مجید حفظ کرنے میں آسانی، استقامت اور برکت عطا فرمائے۔ آمین۔

جزاکم اللہ خیراً

{InstituteName}
    """.trimIndent(),
    val teacherSignatureTitle: String = "قاری عبدالرحمن",
    val teacherDesignation: String = "مدرس شعبہ حفظ",
    val officialDua: String = "بارک اللہ فی علمہ وعملہ، اللہ تعالیٰ عزیز طالب علم کو حامل قرآن و عامل بالقرآن بنائے۔ آمین",
    val themeMode: String = "DARK" // "DARK", "LIGHT", "SYSTEM"
)
