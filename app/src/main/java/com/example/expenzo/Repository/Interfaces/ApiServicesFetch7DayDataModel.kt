package com.example.expenzo.Repository.Interfaces

import com.example.expenzo.Model.FetchCurrentDayDataModel
import com.example.expenzo.Model.FetchCurrentDayDataModel7days
import com.example.expenzo.Model.FetchCurrentDayDataResponse
import com.example.expenzo.Model.FetchCurrentDayDataResponse7days
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServicesFetch7DayDataModel {
    @POST("dev/api/fetchSevenDayData")
    suspend fun getAll7daysData(@Body request: FetchCurrentDayDataModel7days):Response<FetchCurrentDayDataResponse7days>
}