package io.homeassistant.companion.android.biometric

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.homeassistant.companion.android.R
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BiometricInput(private val activity: FragmentActivity) {

    suspend fun authenticate(): BioMetricResult = suspendCancellableCoroutine { continuation ->
        val biometricPrompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    continuation.resume(BioMetricResult.Error(errString.toString()))
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    continuation.resume(BioMetricResult.Success)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    continuation.resume(BioMetricResult.Error("The authentication failed"))
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.app_name))
            .setDeviceCredentialAllowed(true)
            .build()

        continuation.invokeOnCancellation {
            biometricPrompt.cancelAuthentication()
        }

        biometricPrompt.authenticate(promptInfo)
    }
}

sealed class BioMetricResult {
    object Success : BioMetricResult()
    class Error(val message: String) : BioMetricResult()
}