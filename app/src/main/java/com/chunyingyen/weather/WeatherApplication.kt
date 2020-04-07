package com.chunyingyen.weather

import android.app.Application

class WeatherApplication: Application() {
    companion object{
        lateinit var context: WeatherApplication
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}