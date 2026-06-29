package com.example.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

object AppStrings {
    var currentLang = "en"
    
    val dashboard: String get() = if (currentLang == "hi") "डैशबोर्ड" else "Dashboard"
    val history: String get() = if (currentLang == "hi") "इतिहास" else "History"
    val profile: String get() = if (currentLang == "hi") "प्रोफ़ाइल" else "Profile & Settings"
    val upiAlert: String get() = if (currentLang == "hi") "यूपीआई अलर्ट" else "UPI Alert"
    val user: String get() = if (currentLang == "hi") "उपयोगकर्ता" else "User"
    val accountActive: String get() = if (currentLang == "hi") "खाता सक्रिय" else "Account Active"
    val changePin: String get() = if (currentLang == "hi") "पिन बदलें" else "Change PIN"
    val logout: String get() = if (currentLang == "hi") "लॉग आउट" else "Logout"
    val language: String get() = if (currentLang == "hi") "भाषा" else "Language"
    val backupRestore: String get() = if (currentLang == "hi") "बैकअप और पुनर्स्थापना" else "Backup & Restore"
    val exportBackup: String get() = if (currentLang == "hi") "बैकअप निर्यात करें" else "Export Backup"
    val importBackup: String get() = if (currentLang == "hi") "बैकअप आयात करें" else "Import Backup"
    val soundVibration: String get() = if (currentLang == "hi") "ध्वनि और कंपन" else "Sound & Vibration"
    val paymentSound: String get() = if (currentLang == "hi") "भुगतान ध्वनि" else "Payment Sound"
    val vibrateOnPayment: String get() = if (currentLang == "hi") "भुगतान पर कंपन" else "Vibrate on Payment"
    val notificationAccess: String get() = if (currentLang == "hi") "अधिसूचना पहुंच" else "Notification Access"
    val notificationDesc: String get() = if (currentLang == "hi") "भुगतान का पता लगाने के लिए सूचनाएं पढ़ने की अनुमति दें।" else "Allow this app to read notifications to detect payments."
    val openSettings: String get() = if (currentLang == "hi") "सेटिंग्स खोलें" else "Open Notification Settings"
    val todayCollection: String get() = if (currentLang == "hi") "आज का संग्रह" else "Today's Collection"
    val totalCollection: String get() = if (currentLang == "hi") "कुल संग्रह" else "Total Collection"
    val recentPayments: String get() = if (currentLang == "hi") "हाल के भुगतान" else "Recent Payments"
    val noPayments: String get() = if (currentLang == "hi") "अभी तक कोई भुगतान नहीं" else "No payments yet"
    val allPayments: String get() = if (currentLang == "hi") "सभी भुगतान" else "All Payments"
    val voiceSettings: String get() = if (currentLang == "hi") "आवाज़ सेटिंग्स" else "Voice Settings"
    val customVoiceTitle: String get() = if (currentLang == "hi") "कस्टम आवाज़" else "Custom Voice"
    val customMessageTitle: String get() = if (currentLang == "hi") "कस्टम संदेश" else "Custom Message"
    val customMessageDesc: String get() = if (currentLang == "hi") "{amount} और {app} का प्रयोग करें" else "Use {amount} and {app}"
}
