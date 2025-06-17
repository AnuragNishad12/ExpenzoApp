package com.example.expenzo.Repository.Interfaces

import com.example.expenzo.Model.FetchCurrentDayDataModel
import com.example.expenzo.Model.FetchCurrentDayDataResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServicesFetchCurrentDayDataModel {
    @POST("dev/api/fetchCurrentDayData")
    suspend fun getAllCurrentData(@Body request: FetchCurrentDayDataModel):Response<FetchCurrentDayDataResponse>
}