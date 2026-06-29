package com.example.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, language: String): ContextWrapper {
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return ContextWrapper(context.createConfigurationContext(config))
    }
}
