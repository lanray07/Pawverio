package com.pawverio.platform

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent

/**
 * Optional same-device app lock. Caller locks content until Activity.RESULT_OK, including on
 * resume.
 */
class DeviceLock(context: Context) {
    private val manager = context.getSystemService(KeyguardManager::class.java)
    private val preferences = context.getSharedPreferences("device-lock", Context.MODE_PRIVATE)
    val available: Boolean
        get() = manager.isDeviceSecure

    var enabled: Boolean
        get() = preferences.getBoolean("enabled", false) && available
        set(value) {
            require(!value || available)
            preferences.edit().putBoolean("enabled", value).apply()
        }

    @Suppress("DEPRECATION")
    fun confirmationIntent(title: String, description: String): Intent? =
        if (available) manager.createConfirmDeviceCredentialIntent(title, description) else null
}
