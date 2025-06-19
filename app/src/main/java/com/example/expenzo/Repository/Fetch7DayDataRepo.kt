package com.example.expenzo.Repository


import com.example.expenzo.Model.FetchCurrentDayDataModel7days
import com.example.expenzo.Model.FetchCurrentDayDataResponse7days
import retrofit2.Response

class Fetch7DayDataRepo {

    private val apiServices = RetrofitClass.apiServicesFetch7days;

    suspend fun fetchAllCurrentDayData(request: FetchCurrentDayDataModel7days):Response<FetchCurrentDayDataResponse7days>{
        return apiServices.getAll7daysData(request);
    }


}