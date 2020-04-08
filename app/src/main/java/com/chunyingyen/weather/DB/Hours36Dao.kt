package com.chunyingyen.weather.DB

import androidx.room.*
import com.chunyingyen.weather.DB.WeatherDatabase.Companion.tableHours36

@Dao
interface Hours36Dao {

    @Query("SELECT maxData FROM $tableHours36 WHERE city = :city")
    fun getMaxDataStr(city: String): String

    @Query("UPDATE $tableHours36 SET maxData = :new WHERE city = :city")
    fun updateMaxData(city: String, new: String)

    @Query("SELECT minData FROM $tableHours36 WHERE city = :city")
    fun getMinDataStr(city: String): String

    @Query("SELECT minData FROM $tableHours36")
    fun getMinDataStr(): List<String>

    @Query("UPDATE $tableHours36 SET minData = :new WHERE city = :city")
    fun updateMinData(city: String, new: String)

    @Query("DELETE FROM $tableHours36")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: Hour36Entity)

    @Update
    fun update(entity: Hour36Entity)

    @Query("SELECT * FROM $tableHours36")
    fun getAll(): MutableList<Hour36Entity>

    @Delete
    fun delete(entity: Hour36Entity)


}