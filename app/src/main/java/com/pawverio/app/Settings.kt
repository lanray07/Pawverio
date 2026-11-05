package com.pawverio.app

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.settingsStore by preferencesDataStore("pawverio_settings")

data class Preferences(
    val onboarded: Boolean = false,
    val language: String = "",
    val dark: Boolean = false,
    val pounds: Boolean = false,
    val spoken: Boolean = true,
    val emergency: String = "",
    val instructions: String = "",
    val highContrast: Boolean = false,
)

class Settings(private val context: Context) {
    val flow =
        context.settingsStore.data.map {
            Preferences(
                it[booleanPreferencesKey("onboarded")] ?: false,
                it[stringPreferencesKey("language")] ?: "",
                it[booleanPreferencesKey("dark")] ?: false,
                it[booleanPreferencesKey("pounds")] ?: false,
                it[booleanPreferencesKey("spoken")] ?: true,
                it[stringPreferencesKey("emergency")] ?: "",
                it[stringPreferencesKey("instructions")] ?: "",
                it[booleanPreferencesKey("high_contrast")] ?: false,
            )
        }

    suspend fun set(key: String, value: Boolean) {
        context.settingsStore.edit { it[booleanPreferencesKey(key)] = value }
    }

    suspend fun set(key: String, value: String) {
        context.settingsStore.edit { it[stringPreferencesKey(key)] = value }
    }
}
