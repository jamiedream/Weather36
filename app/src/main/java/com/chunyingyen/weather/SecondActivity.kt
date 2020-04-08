package com.chunyingyen.weather

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chunyingyen.weather.data.Hours36key.END_TIME
import com.chunyingyen.weather.data.Hours36key.NAME
import com.chunyingyen.weather.data.Hours36key.START_TIME
import com.chunyingyen.weather.data.Hours36key.UNIT

class SecondActivity: AppCompatActivity() {

    private val TAG = "SecondActivity"

    private val content by lazy { findViewById<TextView>(R.id.content) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        intent.extras?.let {
            content.text =
                String.format(
                    "%s\n%s\n%s\n%s",
                    it.getString(START_TIME),
                    it.getString(END_TIME),
                    it.getString(NAME),
                    it.getString(UNIT))
        }

    }

}