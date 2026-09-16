package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "whatsapp_deliveries")
data class WhatsAppDeliveryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val studentId: Long,
    val studentName: String,
    val reportId: Long,
    val reportType: String, // "DAILY" or "MONTHLY"
    val parentNumber: String,
    val message: String,
    val fileUrl: String? = null,
    val status: String = "Sent", // "Pending", "Sent", "Failed"
    val sentAt: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)
