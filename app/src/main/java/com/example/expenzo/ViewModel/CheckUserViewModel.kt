package com.example.expenzo.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenzo.Model.CheckUserDataClass
import com.example.expenzo.Model.checkUserResponse
import com.example.expenzo.Repository.CheckUserRepository
import com.example.expenzo.Utils.UserDataStore
import kotlinx.coroutines.launch
import org.json.JSONObject


class CheckUserViewModel : ViewModel(){

    private val respository = CheckUserRepository()
    val createUserResult = MutableLiveData<checkUserResponse?>()
    val errorMessage = MutableLiveData<String?>()
    val navigateToSignUpScreen = MutableLiveData<Boolean>()



    fun checkUserViewModel(data: CheckUserDataClass){
        viewModelScope.launch {
            try {
                val response = respository.checkUserCredientialRepo(data)
                if (response.isSuccessful) {
                    createUserResult.postValue(response.body())
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val message = errorJson.optString("message", "Unknown error")
                    errorMessage.postValue("Error: $message")
                    Log.d("UniqueName","name ${message}")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Exception: ${e.localizedMessage}")

            }

        }
    }



    fun checkUserDatainDataStore(context: Context) {

        val userDataStore = UserDataStore(context)
        viewModelScope.launch {
            try {
                val uniqueName = userDataStore.getUniqueName()
                if (!uniqueName.isNullOrEmpty()) {
                    val data = CheckUserDataClass(uniqueInBody = uniqueName)

                    checkUserViewModel(data)
                } else {
                    errorMessage.postValue("No unique name found in DataStore")
                    navigateToSignUpScreen.postValue(true)
                }
            } catch (e: Exception) {
                errorMessage.postValue("Exception reading from DataStore: ${e.localizedMessage}")
            }
        }
    }





}