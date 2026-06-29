package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "payment_history")
data class PaymentHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val date: String,
    val time: String,
    val sourceApp: String,
    val senderName: String,
    val timestamp: Long = System.currentTimeMillis()
)
