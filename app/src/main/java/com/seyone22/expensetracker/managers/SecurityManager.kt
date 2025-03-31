package com.seyone22.expensetracker.managers

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SecurityManager(private val cryptoManager: CryptoManager = CryptoManager()) {
    private val _isSecureScreenEnabled = MutableStateFlow(false)
    val isSecureScreenEnabled: StateFlow<Boolean> = _isSecureScreenEnabled

    fun saveSecureScreenSetting(context: Context?, isSecure: Boolean) {
        if (context == null) return
        val cipher = cryptoManager.initEncryptionCipher("SecureScreenKey")
        val encryptedData = cryptoManager.encrypt(isSecure.toString(), cipher)

        cryptoManager.saveToPrefs(
            encryptedData, context, "secure_prefs", Context.MODE_PRIVATE, "secure_screen"
        )
    }

    fun getSecureScreenSetting(context: Context): Boolean {
        val encryptedData = cryptoManager.getFromPrefs(
            context, "secure_prefs", Context.MODE_PRIVATE, "secure_screen"
        ) ?: return false

        val cipher = cryptoManager.initDecryptionCipher(
            "SecureScreenKey",
            encryptedData.initializationVector
        )
        val decryptedValue = cryptoManager.decrypt(encryptedData.ciphertext, cipher).toBoolean()

        _isSecureScreenEnabled.value = decryptedValue
        return decryptedValue
    }
}