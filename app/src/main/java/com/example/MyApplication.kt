package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.PaymentRepository
import com.example.data.SettingsRepository
import com.example.services.TTSManager
import com.example.utils.AppOpenAdManager
import com.example.data.AuthManager

class MyApplication : Application() {
    
    lateinit var database: AppDatabase
        private set
        
    lateinit var repository: PaymentRepository
        private set
        
    lateinit var settingsRepository: SettingsRepository
        private set
        
    lateinit var authManager: AuthManager
        private set
        
    lateinit var ttsManager: TTSManager
        private set
        
    private lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "upi_payment_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        repository = PaymentRepository(database.paymentDao())
        settingsRepository = SettingsRepository(database.settingsDao())
        authManager = AuthManager(this)
        ttsManager = TTSManager(this)
        
        appOpenAdManager = AppOpenAdManager(this)
        appOpenAdManager.loadAd(this)
    }
}
