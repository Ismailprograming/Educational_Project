package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1L,
    val name: String = "قاری عبدالرحمن",
    val fatherName: String = "مولانا عبدالحمید",
    val phone: String = "+92 300 1234567",
    val email: String = "qari.ar@madrasa.edu",
    val profileImage: String = "",
    val designation: String = "صدر مدرس - شعبہ تجوید و تحفیظ القرآن الکریم",
    val instituteId: Long = 1L,
    val pin: String = "1234",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
