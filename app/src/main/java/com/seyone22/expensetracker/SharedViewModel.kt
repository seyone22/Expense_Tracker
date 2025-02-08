package com.seyone22.expensetracker

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Metadata
import com.seyone22.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.seyone22.expensetracker.data.repository.metadata.MetadataRepository
import com.seyone22.expensetracker.utils.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class SharedViewModel(
    private val metadataRepository: MetadataRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository
) : ViewModel() {
    private val _isSecureScreenEnabled = MutableStateFlow(false)
    val isSecureScreenEnabled: StateFlow<Boolean> = _isSecureScreenEnabled

    val baseCurrencyIdFlow: Flow<Metadata?> =
        metadataRepository.getMetadataByNameStream("BASECURRENCYID")

    // Flow to retrieve and convert "ISUSED" metadata to a boolean
    val isUsedFlow: Flow<Boolean> = metadataRepository.getMetadataByNameStream("ISUSED")
        .map { metadata -> metadata?.infoValue == "TRUE" }

    // Stream for currency format using the base currency ID
    val baseCurrencyFlow: Flow<CurrencyFormat?> =
        baseCurrencyIdFlow.combine(currencyFormatsRepository.getAllCurrencyFormatsStream()) { baseCurrencyId, allCurrencyFormats ->
            // Use the baseCurrencyId to get the corresponding CurrencyFormat
            allCurrencyFormats.firstOrNull { it.currencyId == baseCurrencyId?.infoValue?.toInt() }
        }
    //serve it globally

    // Common functions
    suspend fun getCurrencyById(currencyId: Int): CurrencyFormat? {
        val stream = currencyFormatsRepository.getCurrencyFormatStream(currencyId)
        return stream.firstOrNull()
    }

    fun saveSecureScreenSetting(context: Context?, isSecure: Boolean) {
        if (context == null) return

        val cryptoManager = CryptoManager()
        val cipher = cryptoManager.initEncryptionCipher("SecureScreenKey")

        val encryptedData = cryptoManager.encrypt(isSecure.toString(), cipher)

        Log.d("SecureScreen", "Saving secure screen setting: $isSecure")

        cryptoManager.saveToPrefs(
            encryptedData,
            context,
            "secure_prefs",
            Context.MODE_PRIVATE,
            "secure_screen"
        )
    }

    fun getSecureScreenSetting(context: Context): Boolean {
        val cryptoManager = CryptoManager()
        val encryptedData = cryptoManager.getFromPrefs(
            context,
            "secure_prefs",
            Context.MODE_PRIVATE,
            "secure_screen"
        ) ?: return false

        val cipher = cryptoManager.initDecryptionCipher(
            "SecureScreenKey",
            encryptedData.initializationVector
        )
        val decryptedValue = cryptoManager.decrypt(encryptedData.ciphertext, cipher).toBoolean()

        Log.d("SecureScreen", "Retrieved secure screen setting: $decryptedValue")

        _isSecureScreenEnabled.value = decryptedValue
        return decryptedValue
    }
}



