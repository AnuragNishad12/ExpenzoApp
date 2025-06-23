package com.example.expenzo.Repository

import com.example.expenzo.Model.FetchCurrentDayDataModel30days
import com.example.expenzo.Model.FetchCurrentDayDataResponse30days
import retrofit2.Response

class Fetch30DayDataRepo {

    private val apiServices = RetrofitClass.apiServicesFetch30days;

    suspend fun fetchAllCurrentDayData(request: FetchCurrentDayDataModel30days):Response<FetchCurrentDayDataResponse30days>{
        return apiServices.getAll30daysData(request);
    }

}