package hm.dev.charginganimation.ui

import android.app.KeyguardManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import hm.dev.charginganimation.R
import hm.dev.charginganimation.databinding.ActivityTestBinding
import hm.dev.charginganimation.services.BatteryLevelReceiver
import hm.dev.charginganimation.services.BatteryService
import java.text.SimpleDateFormat
import java.util.*


class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding
    private lateinit var receiver: BatteryLevelReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTestBinding.inflate(layoutInflater)

        showOnLock()

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
    fun showOnLock(){

        window.setBackgroundDrawableResource(android.R.color.transparent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            window.addFlags(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

            )
        }
    }

}