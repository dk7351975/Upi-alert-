package com.example.services

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.MyApplication
import com.example.data.PaymentHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

class PaymentNotificationService : NotificationListenerService() {

    private val supportedApps = listOf(
        "com.phonepe.app",
        "com.google.android.apps.nbu.paisa.user",
        "net.one97.paytm",
        "com.bharatpe.app",
        "in.amazon.mShop.android.shopping",
        "com.phonepe.app.business"
    )

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            val packageName = it.packageName
            if (supportedApps.contains(packageName)) {
                val extras = it.notification.extras
                val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
                val text = extras.getString(Notification.EXTRA_TEXT) ?: ""
                
                Log.d("UPIAlert", "Notification from $packageName: Title: $title, Text: $text")

                // Simple check if it's a receive notification
                val content = "$title $text".lowercase(Locale.ROOT)
                if (content.contains("received") || content.contains("प्राप्त") || content.contains("credited")) {
                    extractPaymentInfo(packageName, title, text)
                }
            }
        }
    }

    private fun extractPaymentInfo(packageName: String, title: String, text: String) {
        val fullText = "$title $text"
        val amountRegex = "(?:(?i)rs\\.?|₹)\\s*(\\d+(?:,\\d+)*(?:\\.\\d+)?)"
        val pattern = Pattern.compile(amountRegex)
        val matcher = pattern.matcher(fullText)

        if (matcher.find()) {
            val amountStr = matcher.group(1)?.replace(",", "") ?: "0"
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            
            if (amount > 0) {
                // Determine source app
                val sourceApp = when (packageName) {
                    "com.phonepe.app", "com.phonepe.app.business" -> "PhonePe"
                    "com.google.android.apps.nbu.paisa.user" -> "Google Pay"
                    "net.one97.paytm" -> "Paytm"
                    "com.bharatpe.app" -> "BharatPe"
                    "in.amazon.mShop.android.shopping" -> "Amazon Pay"
                    else -> "Other UPI"
                }
                
                // Attempt to extract sender (basic attempt)
                var senderName = "Unknown"
                val fromIndex = fullText.lowercase(Locale.ROOT).indexOf("from ")
                if (fromIndex != -1) {
                    val sub = fullText.substring(fromIndex + 5)
                    senderName = sub.split(" ")[0].take(15) // simple heuristic
                }

                savePaymentAndAlert(amount, sourceApp, senderName)
            }
        }
    }

    private fun savePaymentAndAlert(amount: Double, sourceApp: String, senderName: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = dateFormat.format(Date())
        val time = timeFormat.format(Date())

        val payment = PaymentHistory(
            amount = amount,
            date = date,
            time = time,
            sourceApp = sourceApp,
            senderName = senderName
        )

        // Save to DB and handle alerts
        val app = applicationContext as MyApplication
        CoroutineScope(Dispatchers.IO).launch {
            // Get settings
            val settingsFlow = app.settingsRepository.settings
            val settings = settingsFlow.first() ?: com.example.data.AppSettings()
            
            // Check minimum amount limit
            if (amount < settings.minimumAmount) {
                Log.d("UPIAlert", "Payment ignored: Amount ($amount) is less than minimum (${settings.minimumAmount})")
                return@launch
            }
            
            app.repository.insertPayment(payment)
            
            // Play Sound
            when (settings.soundType) {
                "Default" -> playDefaultSound()
                "Bell" -> playSoundAsset("bell.mp3")
                "Cash Register" -> playSoundAsset("cash_register.mp3")
                "Custom" -> settings.customSoundUri?.let { playCustomSound(it) }
            }
            
            // Vibrate
            if (settings.vibration) {
                vibrateDevice()
            }
            
            // Voice Alert
            val integerAmount = amount.toInt()
            var speechText = settings.customMessage
                .replace("{amount}", integerAmount.toString())
                .replace("{app}", sourceApp)
            
            app.ttsManager.speak(speechText, voiceType = settings.voiceType)
        }
    }
    
    private fun playDefaultSound() {
        try {
            val notification = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
            val r = android.media.RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun playSoundAsset(fileName: String) {
        try {
            val afd = applicationContext.assets.openFd(fileName)
            val player = android.media.MediaPlayer()
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player.prepare()
            player.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun playCustomSound(uriString: String) {
        try {
            val uri = android.net.Uri.parse(uriString)
            val player = android.media.MediaPlayer()
            player.setDataSource(applicationContext, uri)
            player.prepare()
            player.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun vibrateDevice() {
        try {
            val vibrator = applicationContext.getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(android.os.VibrationEffect.createOneShot(500, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
