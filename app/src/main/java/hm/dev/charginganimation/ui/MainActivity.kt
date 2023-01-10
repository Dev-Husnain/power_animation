package hm.dev.charginganimation.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import hm.dev.charginganimation.R
import hm.dev.charginganimation.databinding.ActivityMainBinding
import hm.dev.charginganimation.services.BatteryLevelReceiver
import hm.dev.charginganimation.services.BatteryService
import hm.dev.charginganimation.utils.MyConstants

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: BatteryLevelReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        startBroadCastReceiver()


    }

    private fun startBroadCastReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)


        receiver = BatteryLevelReceiver()
        this.registerReceiver(receiver, filter)


        MyConstants.batteryLevel.observe(this@MainActivity) {
            binding.batteryRemain.text = MyConstants.batteryLevel.value.toString()
        }

        MyConstants.batteryTypeUSB.observe(this) {
            if (MyConstants.batteryTypeUSB.value == true) {
                binding.usbType.text = getString(R.string.usb)
            }
        }
        MyConstants.batteryTypeAC.observe(this) {
            if (MyConstants.batteryTypeAC.value == true) {
                binding.aCType.text = getString(R.string.ac_con)
            }

        }
        MyConstants.isConnected.observe(this) {
            if (MyConstants.isConnected.value == true) {
                binding.isConnected.text = getString(R.string.connected)
            } else {
                binding.isConnected.text = getString(R.string.not_connected)
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, Intent(this, BatteryService::class.java))
        } else {
            this.startService(Intent(applicationContext, BatteryService::class.java))
        }

    }
}