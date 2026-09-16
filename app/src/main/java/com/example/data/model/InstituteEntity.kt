package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "institutes")
data class InstituteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1L,
    val name: String = "جامعہ اسلامیہ لتحفیظ القرآن",
    val campus: String = "مرکزی کیمپس، سیکٹر جی-۹، اسلام آباد",
    val phone: String = "+92 51 2233445",
    val whatsapp: String = "+92 300 1234567",
    val email: String = "info@madrasa.edu.pk",
    val logoUrl: String = "",
    val affiliationNo: String = "وفاق المدارس الحاق نمبر: 4892",
    val officialDua: String = "بارک اللہ فی علمہ وعملہ، اللہ تعالیٰ عزیز طالب علم کو حامل قرآن و عامل بالقرآن بنائے۔ آمین",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
