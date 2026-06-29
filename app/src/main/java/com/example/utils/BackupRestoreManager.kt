package com.example.utils

import android.content.Context
import android.net.Uri
import com.example.data.PaymentHistory
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object BackupRestoreManager {
    
    private val jsonFormat = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true
    }

    fun exportBackup(context: Context, uri: Uri, payments: List<PaymentHistory>): Boolean {
        return try {
            val jsonString = jsonFormat.encodeToString(payments)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonString.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importBackup(context: Context, uri: Uri): List<PaymentHistory>? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                jsonFormat.decodeFromString<List<PaymentHistory>>(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
