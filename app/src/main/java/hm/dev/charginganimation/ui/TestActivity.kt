package hm.dev.charginganimation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hm.dev.charginganimation.services.BatteryLevelReceiver
import hm.dev.charginganimation.databinding.ActivityTestBinding
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


        setTimeAndBattery()



    }

    private fun setTimeAndBattery() {
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
            append(MyConstants.batteryLevel.value.toString())
            append("%")
        }

    }
}