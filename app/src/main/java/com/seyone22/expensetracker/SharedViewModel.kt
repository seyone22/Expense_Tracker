package com.seyone22.expensetracker

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seyone22.expensetracker.data.externalApi.infoEuroApi.InfoEuroApi
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Metadata
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.Tag
import com.seyone22.expensetracker.data.repository.category.CategoriesRepository
import com.seyone22.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.seyone22.expensetracker.data.repository.currencyHistory.CurrencyHistoryRepository
import com.seyone22.expensetracker.data.repository.metadata.MetadataRepository
import com.seyone22.expensetracker.data.repository.payee.PayeesRepository
import com.seyone22.expensetracker.data.repository.tag.TagsRepository
import com.seyone22.expensetracker.utils.CryptoManager
import com.seyone22.expensetracker.utils.SnackbarManager
import com.seyone22.expensetracker.utils.updateCurrencyFormatsAndHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharedViewModel(
    private val metadataRepository: MetadataRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository,
    private val currencyHistoryRepository: CurrencyHistoryRepository,
    private val categoriesRepository: CategoriesRepository,
    private val payeesRepository: PayeesRepository,
    private val tagsRepository: TagsRepository
) : ViewModel() {
    val categoriesFlow: Flow<List<Category>> = categoriesRepository.getAllCategoriesStream()
    val payeesFlow: Flow<List<Payee>> = payeesRepository.getAllPayeesStream()
    val tagsFlow: Flow<List<Tag>> = tagsRepository.getAllTagsStream()
    val currenciesFlow: Flow<List<CurrencyFormat>> =
        currencyFormatsRepository.getAllCurrencyFormatsStream()

    private val _isSecureScreenEnabled = MutableStateFlow(false)
    val isSecureScreenEnabled: StateFlow<Boolean> = _isSecureScreenEnabled

    private val baseCurrencyIdFlow: Flow<Metadata?> =
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

    // Initialize the ViewModel and trigger `getMonthlyRates` if `isUsed` is false
    init {
        // Automatically call `getMonthlyRates` when `isUsed` is false
        viewModelScope.launch {
            isUsedFlow.collect { isUsed ->
                if (!isUsed) {
                    //getMonthlyRates()
                }
            }
        }
    }

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

    fun getMonthlyRates() {
        viewModelScope.launch {
            SnackbarManager.showMessage("Updating Currency Formats...", SnackbarDuration.Long)
            try {
                val onlineData = withContext(Dispatchers.IO) {
                    InfoEuroApi.retrofitService.getMonthlyRates()
                }

                val baseCurrency = baseCurrencyFlow.first()
                if (baseCurrency !== null) {
                    updateCurrencyFormatsAndHistory(
                        onlineData = onlineData,
                        baseCurrency = baseCurrency,
                        currencyFormatsRepository = currencyFormatsRepository,
                        currencyHistoryRepository = currencyHistoryRepository
                    )
                }

                // Show success Snackbar **after** both operations are completed
                SnackbarManager.showMessage("Currency formats updated successfully!")

            } catch (e: Exception) {
                SnackbarManager.showMessage("Failed to update currency formats: ${e.message}")
                Log.e("TAG", "Error updating currency formats", e)
            }
        }
    }
}
