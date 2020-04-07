package com.chunyingyen.weather.data

import com.google.gson.annotations.SerializedName

data class Hours36WeatherData (
    @SerializedName("cwbopendata") val cwbopendata: OpenData
)

data class OpenData (
    @SerializedName("dataset") val dataset: DatasetData
)

data class DatasetData (
    @SerializedName("datasetInfo") val datasetInfo: DatasetInfoData,
    @SerializedName("location") val location: MutableList<LocationData>
)

data class DatasetInfoData (
    @SerializedName("datasetDescription") val datasetDescription: String,
    @SerializedName("issueTime") val issueTime: String,
    @SerializedName("update") val update: String
)

data class LocationData (
    @SerializedName("locationName") val locationName: String,
    @SerializedName("weatherElement") val weatherElement: MutableList<WeatherElementData>
)

data class WeatherElementData (
    @SerializedName("elementName") val elementName: String,
    @SerializedName("time") val time: MutableList<TimeData>
)

data class TimeData(
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("parameter") val parameter: ParameterData
)

data class ParameterData (
    @SerializedName("parameterName") val parameterName: String,
    @SerializedName("parameterUnit") val parameterUnit: String
)