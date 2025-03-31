package com.seyone22.expensetracker.managers

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val PREFS_FILENAME = "secure_prefs"
private const val PREF_KEY = "screen_lock_enabled"

class ScreenLockManager(
    private val context: Context,
    private val cryptoManager: CryptoManager,
    private val biometricAuthLauncher: ActivityResultLauncher<Unit>? = null // Pass the launcher to the constructor
) : DefaultLifecycleObserver {
    private val _isLocked = MutableStateFlow(true)

    // Public exposure
    val isAppLocked: StateFlow<Boolean> = _isLocked

    fun toggleLockState() {
        _isLocked.value = !_isLocked.value
    }

    fun triggerLock() {
        _isLocked.value = true
    }

    fun triggerUnlock() {
        if (biometricAuthLauncher == null) return
        biometricAuthLauncher.launch(Unit)
    }

    // Save the screen lock preference
    fun saveScreenLockPreference(isEnabled: Boolean) {
        val cipher = cryptoManager.initEncryptionCipher("ScreenLockKey")
        val encryptedData = cryptoManager.encrypt(isEnabled.toString(), cipher)
        cryptoManager.saveToPrefs(
            encryptedData, context, PREFS_FILENAME, Context.MODE_PRIVATE, PREF_KEY
        )
    }

    // Retrieve the screen lock preference
    fun isScreenLockEnabled(): Boolean {
        val encryptedData =
            cryptoManager.getFromPrefs(context, PREFS_FILENAME, Context.MODE_PRIVATE, PREF_KEY)
                ?: return false  // Default to false if not found
        val cipher =
            cryptoManager.initDecryptionCipher("ScreenLockKey", encryptedData.initializationVector)
        val decryptedText = cryptoManager.decrypt(encryptedData.ciphertext, cipher)
        return decryptedText.toBoolean()  // Convert back to Boolean
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        if (isScreenLockEnabled()) {
            _isLocked.value = true
            Log.d("ScreenLockManager", "App locked")
        }
    }

}
