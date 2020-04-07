package com.viwave.collaborationproject.http

import com.chunyingyen.weather.data.Hours36WeatherData
import retrofit2.Call
import retrofit2.http.*

interface IHttp {
    @GET("{mid}")
    fun loadData(
        @Path(value = "mid", encoded = true) mid: String,
        @Query("Authorization") token: String,
        @Query("downloadType") downloadType: String = "WEB",
        @Query("format") format: String = "JSON"): Call<Hours36WeatherData>

}