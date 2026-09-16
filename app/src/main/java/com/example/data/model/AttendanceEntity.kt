package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_records")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val studentId: Long,
    val date: String,
    val status: String, // حاضر, غیر حاضر, چھٹی
    val updatedAt: Long = System.currentTimeMillis()
)
