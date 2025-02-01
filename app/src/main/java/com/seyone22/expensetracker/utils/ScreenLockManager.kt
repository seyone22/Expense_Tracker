package com.seyone22.expensetracker.utils

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val PREFS_FILENAME = "secure_prefs"
private const val PREF_KEY = "screen_lock_enabled"

class ScreenLockManager(
    private val application: Application,
    private val context: Context,
    private val cryptoManager: CryptoManager
) : DefaultLifecycleObserver {
    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked

    private fun lockApp() {
        _isLocked.value = true
    }

    private fun unlockApp() {
        _isLocked.value = false
    }

    fun triggerLock() {
        lockApp()
    }

    fun triggerUnlock() {
        unlockApp()
    }

    // Save the screen lock preference
    fun saveScreenLockPreference(isEnabled: Boolean) {
        val cipher = cryptoManager.initEncryptionCipher("ScreenLockKey")
        val encryptedData = cryptoManager.encrypt(isEnabled.toString(), cipher)
        cryptoManager.saveToPrefs(
            encryptedData,
            context,
            PREFS_FILENAME,
            Context.MODE_PRIVATE,
            PREF_KEY
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

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (isScreenLockEnabled()) {
            _isLocked.value = false
        }
    }
}
