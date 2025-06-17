package com.example.expenzo.Repository

import com.example.expenzo.Repository.Interfaces.ApiResponse
import com.example.expenzo.Repository.Interfaces.ApiServiceCheckUser
import com.example.expenzo.Repository.Interfaces.ApiServicesFetchCurrentDayDataModel
import com.example.expenzo.Repository.Interfaces.TransactionApiService
import com.example.expenzo.Repository.Interfaces.TransactionDeleteApiServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClass {
    private const val BASE_URL = "https://q36bgcmr36.execute-api.ap-south-1.amazonaws.com/"
    val apiservices : ApiResponse by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiResponse::class.java)
    }

    val apiservicesCheckUser: ApiServiceCheckUser by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServiceCheckUser::class.java)
    }

    val apiservicesTransactionrepo : TransactionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TransactionApiService::class.java)
    }

    val apiServicesTransactionData: TransactionDeleteApiServices by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TransactionDeleteApiServices::class.java)
    }

    val apiServicesFetchCurrentDay : ApiServicesFetchCurrentDayDataModel by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServicesFetchCurrentDayDataModel::class.java)
    }


}