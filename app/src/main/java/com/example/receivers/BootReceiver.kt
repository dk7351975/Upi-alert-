package com.example.receivers

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.example.services.PaymentNotificationService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            Log.d("BootReceiver", "Device booted. Rebinding NotificationListenerService")
            
            val componentName = ComponentName(context, PaymentNotificationService::class.java)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                android.service.notification.NotificationListenerService.requestRebind(componentName)
            } else {
                val packageManager = context.packageManager
                packageManager.setComponentEnabledSetting(
                    componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
                packageManager.setComponentEnabledSetting(
                    componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }
}
