package com.example.expenzo.Repository.Interfaces

import com.example.expenzo.Model.FetchCurrentDayDataModel30days
import com.example.expenzo.Model.FetchCurrentDayDataResponse30days
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServicesFetch30DayDataModel {
    @POST("dev/api/fetch30DayData")
    suspend fun getAll30daysData(@Body request: FetchCurrentDayDataModel30days):Response<FetchCurrentDayDataResponse30days>
}