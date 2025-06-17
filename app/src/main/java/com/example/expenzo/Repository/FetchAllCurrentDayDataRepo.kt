package com.example.expenzo.Repository

import com.example.expenzo.Model.FetchCurrentDayDataModel
import com.example.expenzo.Model.FetchCurrentDayDataResponse
import retrofit2.Response


class FetchAllCurrentDayDataRepo {
    private val apiServices = RetrofitClass.apiServicesFetchCurrentDay;

    suspend fun fetchAllCurrentDayData(request: FetchCurrentDayDataModel):Response<FetchCurrentDayDataResponse>{
        return apiServices.getAllCurrentData(request);
    }


}