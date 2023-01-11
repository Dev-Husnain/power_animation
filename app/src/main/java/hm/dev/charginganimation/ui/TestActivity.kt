package hm.dev.charginganimation.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import hm.dev.charginganimation.R
import hm.dev.charginganimation.services.BatteryLevelReceiver
import hm.dev.charginganimation.databinding.ActivityTestBinding
import hm.dev.charginganimation.services.BatteryService
import hm.dev.charginganimation.utils.MyConstants
import java.text.SimpleDateFormat
import java.util.*

class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding
    private lateinit var receiver: BatteryLevelReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        this.packageManager.getLaunchIntentForPackage("hm.dev.charginganimation")

        startBroadCastReceiver()

        setTimeAndBattery()



    }

    private fun setTimeAndBattery() {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryRemaining = level / scale.toFloat() * 100
        val simpleTime = SimpleDateFormat("hh:mm a")
        val simpleDate=SimpleDateFormat("EEEE dd MMMM yyyy")
        val currentTime = simpleTime.format(Date())
        val currentDate = simpleDate.format(Date())
        binding.currentTimeDate.text= buildString {
            append(currentTime)
            append(" \n ")
            append(currentDate)
        }
        binding.batteryCharged.text= buildString {
            append((batteryRemaining.toInt()).toString())
            append("%")
        }
    }

    private fun startBroadCastReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)


        receiver = BatteryLevelReceiver()
        this.registerReceiver(receiver, filter)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, Intent(this, BatteryService::class.java))
        } else {
            this.startService(Intent(applicationContext, BatteryService::class.java))
        }

    }
}