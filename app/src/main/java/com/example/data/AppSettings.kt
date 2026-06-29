package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val language: String = "hi",
    val voiceType: String = "Female",
    val soundType: String = "Default",
    val customSoundUri: String? = null,
    val minimumAmount: Double = 1.0,
    val vibration: Boolean = true,
    val darkMode: Boolean = false,
    val customMessage: String = "Received ₹{amount} on {app}"
)
