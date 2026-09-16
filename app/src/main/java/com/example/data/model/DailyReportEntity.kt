package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reports")
data class DailyReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val studentId: Long,
    val teacherId: Long = 1L,
    val date: String, // Format: YYYY-MM-DD e.g. "2026-09-18"
    val dateDisplayUrdu: String = "", // e.g. "۱۸ ستمبر ۲۰۲۶ء"
    // 1. Sabaq
    val sabaqSurah: String = "سورۃ البقرہ",
    val sabaqFromAyah: Int = 1,
    val sabaqToAyah: Int = 10,
    val sabaqLines: Int = 10,
    // 2. Sabqi
    val sabqiJuz: Int = 1,
    val sabqiMistakes: Int = 0,
    // 3. Manzil
    val manzilJuz: Int = 1,
    val manzilMistakes: Int = 0,
    // 4. Attendance: حاضر, غیر حاضر, چھٹی
    val attendance: String = "حاضر",
    // 5. Performance: بہترین, اچھی, مناسب, مزید محنت درکار
    val performance: String = "بہترین",
    // 6. Teacher remarks
    val remarks: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
