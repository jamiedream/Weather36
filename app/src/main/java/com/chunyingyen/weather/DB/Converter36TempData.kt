package com.chunyingyen.weather.DB

import com.chunyingyen.weather.data.Hours36TemperatureData
import com.chunyingyen.weather.data.Hours36key.END_TIME
import com.chunyingyen.weather.data.Hours36key.NAME
import com.chunyingyen.weather.data.Hours36key.START_TIME
import com.chunyingyen.weather.data.Hours36key.UNIT
import com.chunyingyen.weather.data.TimeData
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.json.JSONArray

object Converter36TempData {

    private val TAG = "Converter36TempData"

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
                    this.addProperty(START_TIME, it.startTime)
                    this.addProperty(END_TIME, it.endTime)
                    this.addProperty(NAME, it.name)
                    this.addProperty(UNIT, it.unit)
                }
            )
        }
        return Gson().toJson(array)
    }

    fun getListFromStr(rawData: String): MutableList<Hours36TemperatureData>{
        val dataArray = JsonArray()
        val array = JSONArray(rawData)
        for(num in 0 until array.length()){
            val startTime = array.getJSONObject(num).get(START_TIME).toString()
            val endTime = array.getJSONObject(num).get(END_TIME).toString()
            val name = array.getJSONObject(num).get(NAME).toString()
            val unit = array.getJSONObject(num).get(UNIT).toString()
            dataArray.add(
                JsonObject().apply {
                    this.addProperty(START_TIME, startTime)
                    this.addProperty(END_TIME, endTime)
                    this.addProperty(NAME, name)
                    this.addProperty(UNIT, unit)
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
                        data.asJsonObject.get(START_TIME).asString,
                        data.asJsonObject.get(END_TIME).asString,
                        data.asJsonObject.get(NAME).asString,
                        data.asJsonObject.get(UNIT).asString
                    )
                )
            }
            list
        }
}