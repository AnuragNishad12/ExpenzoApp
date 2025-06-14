package com.example.expenzo.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenzo.Model.CreateUserResponse
import com.example.expenzo.Model.Users
import com.example.expenzo.Repository.UserRepository
import kotlinx.coroutines.launch

import org.json.JSONObject

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    val createUserResult = MutableLiveData<CreateUserResponse?>()
    val errorMessage = MutableLiveData<String?>()

    fun createUser(request: Users) {
        viewModelScope.launch {
            try {
                val response = repository.createUser(request)
                if (response.isSuccessful) {
                    createUserResult.postValue(response.body())
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val message = errorJson.optString("message", "Unknown error")
                    errorMessage.postValue("Error: $message")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }
}
