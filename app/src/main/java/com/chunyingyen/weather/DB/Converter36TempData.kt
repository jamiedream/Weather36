package com.chunyingyen.weather.DB

import android.util.Log
import com.chunyingyen.weather.data.Hours36TemperatureData
import com.chunyingyen.weather.data.TimeData
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.json.JSONArray

object Converter36TempData {

    private val TAG = "Converter36TempData"

//    fun updateDataCount(element: MainActivity.Companion.ELEMENT, city: String){
//        val rawData =
//            when(element){
//                MainActivity.Companion.ELEMENT.MAX ->
//                    WeatherDatabase(context).getHours36Dao().getMaxDataStr(city)
//                MainActivity.Companion.ELEMENT.MIN ->
//                    WeatherDatabase(context).getHours36Dao().getMinDataStr(city)
//            }
//
//        Log.d("updateDataCount: $element", rawData)
//
//        val dataList = getListFromStr(rawData)
//        when(element){
//            MainActivity.Companion.ELEMENT.MAX ->
//                WeatherDatabase(context).getHours36Dao().updateMaxData(city, newJsonArrayStr(dataList))
//            MainActivity.Companion.ELEMENT.MIN ->
//                WeatherDatabase(context).getHours36Dao().updateMinData(city, newJsonArrayStr(dataList))
//        }
//
//        Log.d("updateDataCount: max", WeatherDatabase(context).getHours36Dao().getMaxDataStr(city))
//        Log.d("updateDataCount: min", WeatherDatabase(context).getHours36Dao().getMinDataStr(city))
//    }

    fun getTempDataStr(timeArray: MutableList<TimeData>?): String{
        val dataList = mutableListOf<Hours36TemperatureData>()
        timeArray?.forEach {
            dataList.add(
                Hours36TemperatureData(
                    it.startTime,
                    it.endTime,
                    it.parameter.parameterName,
                    it.parameter.parameterUnit
                )
            )
        }
        return newJsonArrayStr(dataList)
    }

    private fun newJsonArrayStr(list: MutableList<Hours36TemperatureData>): String{
        val array = JsonArray()
        list.forEach {
            array.add(
                JsonObject().apply {
                    this.addProperty("startTime", it.startTime)
                    this.addProperty("endTime", it.endTime)
                    this.addProperty("name", it.name)
                    this.addProperty("unit", it.unit)
                }
            )
        }
        return Gson().toJson(array)
    }

    private fun getListFromStr(rawData: String): MutableList<Hours36TemperatureData>{
        val dataArray = JsonArray()
        val array = JSONArray(rawData)
        for(num in 0 until array.length()){
            val startTime = array.getJSONObject(num).get("startTime").toString()
            val endTime = array.getJSONObject(num).get("endTime").toString()
            val name = array.getJSONObject(num).get("name").toString()
            val unit = array.getJSONObject(num).get("unit").toString()
            dataArray.add(
                JsonObject().apply {
                    this.addProperty("startTime", startTime)
                    this.addProperty("endTime", endTime)
                    this.addProperty("name", name)
                    this.addProperty("unit", unit)
                }
            )
        }
        return GsonBuilder()
                .registerTypeAdapter(object: TypeToken<MutableList<Hours36TemperatureData>>(){}.type, dataListDeserializer)
                .create()
                .fromJson(dataArray, object: TypeToken<MutableList<Hours36TemperatureData>>(){}.type)
    }

    private val dataListDeserializer =
        JsonDeserializer<MutableList<Hours36TemperatureData>> { json, _, _ ->
            val list = mutableListOf<Hours36TemperatureData>()
            json.asJsonArray.forEach {data ->
                list.add(
                    Hours36TemperatureData(
                        data.asJsonObject.get("startTime").asString,
                        data.asJsonObject.get("endTime").asString,
                        data.asJsonObject.get("name").asString,
                        data.asJsonObject.get("unit").asString
                    )
                )
            }
            list
        }
}