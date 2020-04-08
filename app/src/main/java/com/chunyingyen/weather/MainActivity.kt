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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chunyingyen.weather.DB.Converter36TempData
import com.chunyingyen.weather.DB.Hour36Entity
import com.chunyingyen.weather.DB.WeatherDatabase
import com.chunyingyen.weather.adapter.Hours36ListAdapter
import com.chunyingyen.weather.data.Hours36TemperatureData
import com.chunyingyen.weather.data.Hours36WeatherData
import com.chunyingyen.weather.http.Api
import com.viwave.collaborationproject.http.IAPIResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import com.chunyingyen.weather.data.Hours36key.END_TIME
import com.chunyingyen.weather.data.Hours36key.NAME
import com.chunyingyen.weather.data.Hours36key.START_TIME
import com.chunyingyen.weather.data.Hours36key.UNIT

class MainActivity : AppCompatActivity(), IITemClickListener {

    private val TAG = "MainActivity"

    private val maxElement = "MaxT"
    private val minElement = "MinT"

    private val PREFERENCE_INIT = "IS_INIT"
    private val PREFERENCE_DATA_EXIST = "IS_DATA_EXIST"

    private val preference by lazy {
        PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
    }

    private lateinit var progressDialog : AlertDialog

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.view_recycler) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "${preference.getBoolean(PREFERENCE_INIT, false)}")
        if(!preference.getBoolean(PREFERENCE_INIT, false)){
            //init, load data then show view
            preference.edit().putBoolean(PREFERENCE_INIT, true).apply()
            progressDialog = AlertDialog.Builder(this)
                                .setView(R.layout.view_loading)
                                .show()

        }else{
            Toast.makeText(this, "歡迎回來", Toast.LENGTH_LONG).show()
            if(preference.getBoolean(PREFERENCE_DATA_EXIST, false)){
                //show data
                showList()
            }else{
                //load data then show view
                progressDialog = AlertDialog.Builder(this)
                    .setView(R.layout.view_loading)
                    .show()
            }
        }

    }

    override fun onResume() {
        super.onResume()

        if(!isNetworkConnect(applicationContext)){
            progressDialog.dismiss()
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
//                                Log.d("city", "${locationData.locationName}")
//                                Log.d("minData", minTempData)
//                                Log.d("maxData", maxTempData)
                                WeatherDatabase(applicationContext).getHours36Dao().insert(
                                    Hour36Entity(
                                        locationData.locationName,
                                        maxTempData,
                                        minTempData
                                    )
                                )
                            }
                        }

                        showList()
                        progressDialog.dismiss()

                    }
                }

                override fun onFailed(msg: String?) {
                    progressDialog.dismiss()
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

    private fun showList(){
        GlobalScope.launch(Dispatchers.IO) {
            val minDataList =
                mutableListOf<Hours36TemperatureData>().apply {
                    WeatherDatabase(this@MainActivity).getHours36Dao().getMinDataStr().forEach {
                        Converter36TempData.getListFromStr(it).forEach {
                            this.add(it)
                        }
                    }
                }
//            Log.d(TAG, "${minDataList.size}")
            launch(Dispatchers.Main) {
                loadListView(minDataList)
            }
        }
    }

    private fun loadListView(listData: MutableList<Hours36TemperatureData>){
        val bindingListAdapter = Hours36ListAdapter(listData, this)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bindingListAdapter
            val decorator =
                DividerItemDecoration(
                    context,
                    LinearLayoutManager(context).orientation
                )
            decorator.setDrawable(resources.getDrawable(android.R.drawable.divider_horizontal_dark, null))
            addItemDecoration(decorator)
        }
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

    override fun onClick(data: Hours36TemperatureData) {
        val intent = Intent(this, SecondActivity::class.java)
        val bundle = Bundle()
        bundle.putString(START_TIME, data.startTime)
        bundle.putString(END_TIME, data.endTime)
        bundle.putString(NAME, data.name)
        bundle.putString(UNIT, data.unit)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}
