package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_reports")
data class MonthlyReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val studentId: Long,
    val month: Int, // 1 - 12
    val year: Int, // e.g. 2026
    val monthNameUrdu: String = "ستمبر 2026ء",
    val presentDays: Int = 0,
    val absentDays: Int = 0,
    val leaveDays: Int = 0,
    val totalCalendarDays: Int = 30,
    val totalSabaqDays: Int = 0,
    val totalSabaqLines: Int = 0,
    val totalSabqiEntries: Int = 0,
    val totalManzilEntries: Int = 0,
    val totalSabqiMistakes: Int = 0,
    val totalManzilMistakes: Int = 0,
    val averagePerformance: Double = 0.0,
    val sabaqScore: Int = 0,
    val sabqiScore: Int = 0,
    val manzilScore: Int = 0,
    val overallAccuracy: Int = 0,
    val pdfUrl: String? = null,
    val imageUrl: String? = null,
    val generatedAt: Long = System.currentTimeMillis()
)
