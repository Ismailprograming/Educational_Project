package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val fatherName: String,
    val whatsappNumber: String,
    val startingJuz: Int,
    val currentJuz: Int = startingJuz,
    val currentSurah: String = "سورۃ البقرہ",
    val currentAyah: Int = 1,
    val rollNumber: String = "",
    val halqa: String = "حلقہ اول",
    val admissionDate: String = "2026-01-01",
    val isArchived: Boolean = false,
    val instituteId: Long = 1L,
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
