package com.pawverio.platform

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.core.content.ContextCompat
import java.util.Locale

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    UNAVAILABLE,
    PERMISSION_REQUIRED,
    NO_MATCH,
    ERROR,
}

/**
 * Owned by an Activity, invoked on the main thread, closed on destruction. No recordings retained.
 */
class VoiceController(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onState: (VoiceState) -> Unit,
) : AutoCloseable {
    private var recognizer: SpeechRecognizer? = null
    private var ttsReady = false
    private var closed = false
    private val tts =
        TextToSpeech(context.applicationContext) { ttsReady = it == TextToSpeech.SUCCESS }
    var spokenFeedbackEnabled = true
    val recognitionAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    fun start(localeTag: String) {
        if (closed) return
        if (
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED
        ) {
            onState(VoiceState.PERMISSION_REQUIRED)
            return
        }
        if (!recognitionAvailable) {
            onState(VoiceState.UNAVAILABLE)
            return
        }
        try {
            if (recognizer == null) {
                recognizer =
                    SpeechRecognizer.createSpeechRecognizer(context).apply {
                        setRecognitionListener(
                            object : RecognitionListener {
                                override fun onReadyForSpeech(params: Bundle?) =
                                    onState(VoiceState.LISTENING)

                                override fun onBeginningOfSpeech() = Unit

                                override fun onRmsChanged(rmsdB: Float) = Unit

                                override fun onBufferReceived(buffer: ByteArray?) = Unit

                                override fun onEndOfSpeech() = onState(VoiceState.PROCESSING)

                                override fun onError(error: Int) =
                                    onState(
                                        when (error) {
                                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                                                VoiceState.PERMISSION_REQUIRED
                                            SpeechRecognizer.ERROR_NO_MATCH,
                                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                                                VoiceState.NO_MATCH
                                            else -> VoiceState.ERROR
                                        }
                                    )

                                override fun onResults(results: Bundle?) {
                                    onState(VoiceState.IDLE)
                                    results
                                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                                        ?.firstOrNull()
                                        ?.let(onResult)
                                }

                                override fun onPartialResults(partialResults: Bundle?) = Unit

                                override fun onEvent(eventType: Int, params: Bundle?) = Unit
                            }
                        )
                    }
            }
            tts.stop()
            recognizer?.startListening(
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                    )
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeTag)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }
            )
            onState(VoiceState.LISTENING)
        } catch (_: Exception) {
            onState(VoiceState.ERROR)
        }
    }

    fun stop() {
        recognizer?.cancel()
        onState(VoiceState.IDLE)
    }

    /**
     * False means no installed voice supports this language. Touch/text always remain available.
     */
    fun speak(text: String, localeTag: String): Boolean {
        if (!ttsReady || closed || !spokenFeedbackEnabled) return false
        val locale = Locale.forLanguageTag(localeTag)
        if (tts.isLanguageAvailable(locale) < TextToSpeech.LANG_AVAILABLE) return false
        tts.language = locale
        return tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "pawverio-feedback") ==
            TextToSpeech.SUCCESS
    }

    override fun close() {
        closed = true
        recognizer?.destroy()
        recognizer = null
        tts.stop()
        tts.shutdown()
    }
}
