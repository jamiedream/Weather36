package com.viwave.collaborationproject.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HttpManager {

    private val TAG = this::class.java.simpleName
    private val API_URL = "https://opendata.cwb.gov.tw/fileapi/v1/opendataapi/"

    companion object {
        private val manager: HttpManager = HttpManager()
        val client: Retrofit
            get() = manager.retrofit
    }

    private val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()
    }


}