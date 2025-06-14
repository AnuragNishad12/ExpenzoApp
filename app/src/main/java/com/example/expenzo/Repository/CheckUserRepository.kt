package com.example.expenzo.Repository

import android.util.Log
import com.example.expenzo.Model.CheckUserDataClass
import com.example.expenzo.Model.checkUserResponse
import retrofit2.Response
import retrofit2.http.Body


class CheckUserRepository {
//    suspend fun checkUserCredientialRepo(@Body request: CheckUserDataClass): Response<checkUserResponse>{
//        return RetrofitClass.apiservicesCheckUser.checkUserCrediential(request)
//    }

    suspend fun checkUserCredientialRepo(@Body request: CheckUserDataClass): Response<checkUserResponse> {
        Log.d("CheckUserRepo", "Sending request: $request")
        val response = RetrofitClass.apiservicesCheckUser.checkUserCrediential(request)

        if (response.isSuccessful) {
            Log.d("CheckUserRepo", "Received response: ${response.body()}")
        } else {
            Log.e("CheckUserRepo", "API Error Code: ${response.code()}")
            Log.e("CheckUserRepo", "Error Body: ${response.errorBody()?.string()}")
        }

        return response
    }


}