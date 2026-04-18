package com.manish.tripgenie.network

import com.manish.tripgenie.model.SheetSafetyData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SafetyApiService {
    @GET("search")
    fun getCityData(@Query("city") city: String): Call<List<SheetSafetyData>>
}
