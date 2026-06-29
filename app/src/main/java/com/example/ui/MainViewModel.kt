package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppSettings
import com.example.data.PaymentHistory
import com.example.data.PaymentRepository
import com.example.data.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(
    private val repository: PaymentRepository,
    private val settingsRepository: SettingsRepository,
    private val authManager: com.example.data.AuthManager
) : ViewModel() {

    // Filtering states
    val minAmountFilter = MutableStateFlow<Double?>(null)
    val maxAmountFilter = MutableStateFlow<Double?>(null)
    val appFilter = MutableStateFlow<Set<String>>(emptySet())
    val senderNameSearch = MutableStateFlow("")

    val allPayments: StateFlow<List<PaymentHistory>> = repository.allPayments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    val filteredPayments: StateFlow<List<PaymentHistory>> = combine(
        allPayments,
        minAmountFilter,
        maxAmountFilter,
        appFilter,
        senderNameSearch
    ) { payments, min, max, apps, search ->
        payments.filter { payment ->
            val meetsMin = min == null || payment.amount >= min
            val meetsMax = max == null || payment.amount <= max
            val meetsApp = apps.isEmpty() || apps.contains(payment.sourceApp)
            val meetsSearch = search.isBlank() || payment.senderName.contains(search, ignoreCase = true)
            meetsMin && meetsMax && meetsApp && meetsSearch
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .map { it ?: AppSettings() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )
        
    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            settingsRepository.saveSettings(newSettings)
        }
    }
        
    private val _todayDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    
    val todayTotal: StateFlow<Double?> = repository.getTodayTotal(_todayDate.value)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )
        
    val totalCollection: StateFlow<Double?> = repository.getTotalCollection()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )
        
    fun exportBackup(context: android.content.Context, uri: android.net.Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val payments = allPayments.value
            val success = com.example.utils.BackupRestoreManager.exportBackup(context, uri, payments)
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                onResult(success)
            }
        }
    }

    fun importBackup(context: android.content.Context, uri: android.net.Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val payments = com.example.utils.BackupRestoreManager.importBackup(context, uri)
            if (payments != null) {
                repository.clearHistory()
                payments.forEach { payment ->
                    repository.insertPayment(payment.copy(id = 0))
                }
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onResult(true)
                }
            } else {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }

    val isLoggedIn: StateFlow<Boolean> = authManager.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
        
    val mpin: StateFlow<String?> = authManager.mpin
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
        
    val language: StateFlow<String> = authManager.language
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "en"
        )
        
    fun setLoggedIn(loggedIn: Boolean) {
        viewModelScope.launch {
            authManager.setLoggedIn(loggedIn)
        }
    }
    
    fun setMpin(pin: String?) {
        viewModelScope.launch {
            authManager.setMpin(pin)
        }
    }
    
    fun setLanguage(lang: String) {
        viewModelScope.launch {
            authManager.setLanguage(lang)
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authManager.logout()
        }
    }

    class Factory(
        private val repository: PaymentRepository,
        private val settingsRepository: SettingsRepository,
        private val authManager: com.example.data.AuthManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository, settingsRepository, authManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
