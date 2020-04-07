package com.chunyingyen.weather

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chunyingyen.weather.DB.Converter36TempData
import com.chunyingyen.weather.DB.Hour36Entity
import com.chunyingyen.weather.DB.WeatherDatabase
import com.chunyingyen.weather.data.Hours36WeatherData
import com.chunyingyen.weather.http.Api
import com.viwave.collaborationproject.http.IAPIResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val maxElement = "MaxT"
    private val minElement = "MinT"

    companion object{
        enum class ELEMENT{MAX, MIN}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()

        if(!isNetworkConnect(applicationContext)){
            val dialog =
                AlertDialog.Builder(this)
                    .setTitle("無法連線")
                    .setMessage("無法下載資料，請開啟網路")
                    .show()
            GlobalScope.launch(Dispatchers.Main) {
                delay(1000)
                dialog.dismiss()
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            }
        }

        Api.loadData( getString(R.string.memeber_id), getString(R.string.token),
            object : IAPIResult<Hours36WeatherData>{
                override fun onSuccess(res: Response<Hours36WeatherData>) {
                    res.body()?.let { data ->
                        data.cwbopendata.dataset.location.forEach { locationData ->

                            GlobalScope.launch(Dispatchers.IO) {
                                val maxTempData =
                                    Converter36TempData.getTempDataStr(locationData.weatherElement.find { it.elementName == maxElement }?.time)
                                val minTempData =
                                    Converter36TempData.getTempDataStr(locationData.weatherElement.find { it.elementName == minElement }?.time)
                                Log.d("city", "${locationData.locationName}")
                                Log.d("minData", minTempData)
                                Log.d("maxData", maxTempData)
                                WeatherDatabase(applicationContext).getHours36Dao().insert(
                                    Hour36Entity(
                                        locationData.locationName,
                                        maxTempData,
                                        minTempData
                                    )
                                )
                            }
                        }

                    }
                }

                override fun onFailed(msg: String?) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("資料無法下載，請稍後再試")
                        .setMessage(msg)
                        .setPositiveButton("OK"
                        ) { dialog, _ ->
                            dialog.dismiss()
                            this@MainActivity.finish()
                        }
                        .show()
                }

            }
        )



    }

    private fun isNetworkConnect(context: Context): Boolean {
        if (this.isFinishing) {
            return false
        }
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                return when {
                    this.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    this.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    this.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }

        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}
