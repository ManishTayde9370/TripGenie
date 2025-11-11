package com.example.tripgenie.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SheetDBClient {
    private const val BASE_URL = "https://sheetdb.io/api/v1/vhzxdmdfdr61t/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}