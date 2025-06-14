package com.example.expenzo.Utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")


class UserDataStore(private val context: Context) {

   companion object{
       private val UNIQUE_NAME_KEY = stringPreferencesKey("unique_name")
   }

    suspend fun saveUniqueName(uniqueName: String) {
        context.dataStore.edit { preferences ->
            preferences[UNIQUE_NAME_KEY] = uniqueName
        }
    }

    suspend fun getUniqueName(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[UNIQUE_NAME_KEY] }
            .first()
    }


}