package com.chunyingyen.weather.http

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.chunyingyen.weather.data.Hours36WeatherData
import com.viwave.collaborationproject.http.HttpManager
import com.viwave.collaborationproject.http.IAPIResult
import com.viwave.collaborationproject.http.IHttp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Api {

    private val TAG = "Api"

    fun loadData(mid: String, token: String, listener: IAPIResult<Hours36WeatherData>){
        HttpManager.client.create(IHttp::class.java)
            .loadData(mid, token)
            .enqueue(object : Callback<Hours36WeatherData> {
                override fun onFailure(call: Call<Hours36WeatherData>, t: Throwable) {
                    listener.onFailed(t.message)
                }

                override fun onResponse(
                    call: Call<Hours36WeatherData>,
                    response: Response<Hours36WeatherData>
                ) {
                    Log.d(TAG, "${response.body()}")
                    if(response.isSuccessful)
                        listener.onSuccess(response)
                    else
                        listener.onFailed(response.message())
                }

            })
    }
}