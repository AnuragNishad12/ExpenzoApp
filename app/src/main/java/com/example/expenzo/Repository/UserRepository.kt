package com.example.expenzo.Repository

import com.example.expenzo.Model.CreateUserResponse
import com.example.expenzo.Model.Users
import retrofit2.Response


//class UserRepository {
//    suspend fun createUser(request: Users): Response<CreateUserResponse> {
//        return RetrofitClass.apiservices.createUser(request)
//    }
//}

class UserRepository{
    suspend fun createUser(request: Users): Response<CreateUserResponse>{
        return RetrofitClass.apiservices.createUser(request)
    }
}