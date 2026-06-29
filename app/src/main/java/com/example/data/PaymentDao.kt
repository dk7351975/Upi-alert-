package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_history ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<PaymentHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentHistory)

    @Query("DELETE FROM payment_history")
    suspend fun clearHistory()

    @Query("SELECT SUM(amount) FROM payment_history WHERE date = :todayDate")
    fun getTodayTotal(todayDate: String): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM payment_history")
    fun getTotalCollection(): Flow<Double?>
}
