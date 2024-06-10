package com.example.expensetracker.data.repository.userPreferences

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val BASECURRENCYID = intPreferencesKey("baseCurrencyId")
        const val TAG = "UserPreferencesRepo"

    }

    // Error correction, and get data from dataStore
    val baseCurrencyId: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[BASECURRENCYID] ?: -1
        }

    suspend fun saveBaseCurrencyId(baseCurrencyId: Int) {
        dataStore.edit { preferences ->
            preferences[BASECURRENCYID] = baseCurrencyId
        }
    }
}