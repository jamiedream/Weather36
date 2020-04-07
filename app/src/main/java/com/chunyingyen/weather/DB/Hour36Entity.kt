package com.chunyingyen.weather.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chunyingyen.weather.DB.WeatherDatabase.Companion.tableHours36

@Entity(tableName = tableHours36)
data class Hour36Entity(
    @PrimaryKey val city: String,
    @ColumnInfo val maxData: String,
    @ColumnInfo val minData: String
)