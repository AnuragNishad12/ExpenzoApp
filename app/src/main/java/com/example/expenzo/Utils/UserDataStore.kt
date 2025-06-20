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
       private val UNIQUE_ALLDATA_KEY = stringPreferencesKey("alluserData")
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

    suspend fun saveALLuserData(name: String,password:String,email:String,uniqueName:String,MobileNumber:String){


        val combinedData = listOf(name,password,email,uniqueName,MobileNumber).joinToString(",")
        context.dataStore.edit { mydata ->{
            mydata[UNIQUE_ALLDATA_KEY] = combinedData
        }
        }

    }



    suspend fun getAllUsersData(): List<String>?{
        return context.dataStore.data
            .map { response -> response[UNIQUE_ALLDATA_KEY] }.first()?.split(",")
    }







}