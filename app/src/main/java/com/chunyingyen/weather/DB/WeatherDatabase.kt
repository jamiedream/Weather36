package com.chunyingyen.weather.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chunyingyen.weather.DB.WeatherDatabase.Companion.tableVersion

@Database(entities = [Hour36Entity::class], version = tableVersion)
abstract class WeatherDatabase: RoomDatabase() {

    companion object{
        const val tableVersion = 1
        const val tableHours36 = "tableHours36"

        @Volatile private var instance: WeatherDatabase? = null
        private val LOCK = Any()
        const val tableWeather = "tableWeather"
        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context, WeatherDatabase::class.java, tableWeather).build()
    }

    abstract fun getHours36Dao(): Hours36Dao
}