package com.example.services

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TTSManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("hi", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTSManager", "Hindi not supported, trying English")
                tts?.setLanguage(Locale.US)
            }
            isInitialized = true
        } else {
            Log.e("TTSManager", "Initialization Failed!")
        }
    }

    fun speak(text: String, speed: Float = 1.0f, voiceType: String = "Female") {
        if (isInitialized) {
            tts?.setSpeechRate(speed)
            
            // Adjust pitch based on voice type (basic heuristic since actual voice selection depends on TTS engine)
            if (voiceType == "Male") {
                tts?.setPitch(0.5f) // Lower pitch for male
            } else {
                tts?.setPitch(1.0f) // Normal/higher pitch for female
            }
            
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
