package com.example.data

import kotlinx.coroutines.flow.Flow

class PaymentRepository(private val paymentDao: PaymentDao) {
    val allPayments: Flow<List<PaymentHistory>> = paymentDao.getAllPayments()

    fun getTodayTotal(date: String): Flow<Double?> = paymentDao.getTodayTotal(date)
    fun getTotalCollection(): Flow<Double?> = paymentDao.getTotalCollection()

    suspend fun insertPayment(payment: PaymentHistory) {
        paymentDao.insertPayment(payment)
    }

    suspend fun clearHistory() {
        paymentDao.clearHistory()
    }
}
