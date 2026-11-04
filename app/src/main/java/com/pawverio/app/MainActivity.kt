package com.pawverio.app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pawverio.platform.DeviceLock
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var unlocked by mutableStateOf(false)
    private var authenticating = false
    private val authenticate =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            authenticating = false
            unlocked = result.resultCode == RESULT_OK
        }

    override fun attachBaseContext(newBase: Context) {
        val phoneViewport =
            BuildConfig.DEBUG &&
                newBase
                    .getSharedPreferences("qa-capture", Context.MODE_PRIVATE)
                    .getBoolean("phone", false)
        val activityContext =
            if (phoneViewport) {
                val configuration =
                    Configuration(newBase.resources.configuration).apply {
                        densityDpi = 320
                        screenWidthDp = 360
                        screenHeightDp = 640
                        smallestScreenWidthDp = 360
                    }
                newBase.createConfigurationContext(configuration)
            } else newBase
        super.attachBaseContext(activityContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG && intent.getBooleanExtra("qa_capture", false)) {
            requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        val route = if (BuildConfig.DEBUG) intent.getIntExtra("qa_route", 0) else 0
        val phoneViewport =
            BuildConfig.DEBUG &&
                getSharedPreferences("qa-capture", Context.MODE_PRIVATE).getBoolean("phone", false)
        val lock = DeviceLock(this)
        setContent {
            val settings = remember { Settings(applicationContext) }
            val prefs by settings.flow.collectAsStateWithLifecycle(Preferences())
            val deviceConfiguration = LocalConfiguration.current
            val lockConfiguration =
                remember(prefs.language, deviceConfiguration) {
                    Configuration(deviceConfiguration).apply {
                        val locale =
                            if (prefs.language.isBlank()) deviceConfiguration.locales[0]
                            else Locale.forLanguageTag(prefs.language)
                        setLocale(locale)
                        setLayoutDirection(locale)
                    }
                }
            val lockContext =
                remember(lockConfiguration) { createConfigurationContext(lockConfiguration) }
            if (lock.enabled && !unlocked) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides
                        if (
                            lockConfiguration.layoutDirection ==
                                android.view.View.LAYOUT_DIRECTION_RTL
                        )
                            LayoutDirection.Rtl
                        else LayoutDirection.Ltr
                ) {
                    MaterialTheme(colorScheme = pawverioColors(prefs.dark)) {
                        Surface(Modifier.fillMaxSize()) {
                            Column(
                                Modifier.fillMaxSize().padding(32.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    lockContext.getString(R.string.app_name),
                                    style = MaterialTheme.typography.headlineLarge,
                                )
                                Button(
                                    onClick = {
                                        lock
                                            .confirmationIntent(
                                                lockContext.getString(R.string.app_name),
                                                lockContext.getString(R.string.app_lock),
                                            )
                                            ?.let {
                                                authenticating = true
                                                authenticate.launch(it)
                                            }
                                    }
                                ) {
                                    Text(lockContext.getString(R.string.unlock))
                                }
                            }
                        }
                    }
                }
            } else if (phoneViewport) QaPhoneViewport { PawverioApp(route) } else PawverioApp(route)
        }
    }

    override fun onStop() {
        super.onStop()
        if (!authenticating) unlocked = false
    }

    override fun onResume() {
        super.onResume()
        if (DeviceLock(this).enabled)
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        else window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
    }
}
