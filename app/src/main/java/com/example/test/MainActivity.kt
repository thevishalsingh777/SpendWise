package com.example.test

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.example.test.presentation.components.BiometricLockOverlay
import com.example.test.presentation.navigation.MainScaffold
import com.example.test.presentation.theme.SpendWiseTheme
import com.example.test.util.BiometricHelper
import com.example.test.util.BiometricStatus

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = applicationContext as SpendWiseApplication

        setContent {
            val themeMode by app.userPreferences.themeMode.collectAsState()
            val isBiometricEnabled by app.userPreferences.isBiometricEnabled.collectAsState()

            val useDarkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            var isAuthenticated by rememberSaveable { mutableStateOf(!isBiometricEnabled) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            fun triggerUnlock() {
                val status = BiometricHelper.canAuthenticate(this)
                if (status == BiometricStatus.SUCCESS) {
                    BiometricHelper.showBiometricPrompt(
                        activity = this,
                        title = "Unlock SpendWise",
                        subtitle = "Authenticate to access your financial records",
                        onSuccess = {
                            isAuthenticated = true
                            errorMessage = null
                        },
                        onError = { error ->
                            errorMessage = error
                        }
                    )
                } else {
                    isAuthenticated = true
                }
            }

            LaunchedEffect(isBiometricEnabled) {
                if (isBiometricEnabled && !isAuthenticated) {
                    triggerUnlock()
                } else if (!isBiometricEnabled) {
                    isAuthenticated = true
                }
            }

            SpendWiseTheme(darkTheme = useDarkTheme) {
                if (isBiometricEnabled && !isAuthenticated) {
                    BiometricLockOverlay(
                        errorMessage = errorMessage,
                        onUnlockClick = { triggerUnlock() }
                    )
                } else {
                    MainScaffold()
                }
            }
        }
    }
}
