package hm.dev.charginganimation.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER
import android.os.BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER
import android.util.Log
import android.widget.Toast
import hm.dev.charginganimation.ui.Finish
import hm.dev.charginganimation.ui.TestActivity
import hm.dev.charginganimation.utils.MyConstants
import kotlinx.coroutines.*


class BatteryLevelReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {


        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryRemaining = level / scale.toFloat() * 100


            val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL

            val batteryHealth = when(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Over Heat"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
                BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                else -> "Unknown"
            }
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

            val voltage=intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)/1000.0
            val batteryTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0
            val batteryTechnology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)


           // Toast.makeText(context, "Capacity is: $batteryHealth", Toast.LENGTH_SHORT).show()


            val chargePlug: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

            MyConstants.batteryLevel.value = batteryRemaining.toInt()
            MyConstants.isConnected.value = isCharging
            MyConstants.batteryTypeUSB.value = usbCharge
            MyConstants.batteryTypeAC.value = acCharge
//            Toast.makeText(context, "Broadcast running", Toast.LENGTH_SHORT).show()
//
//
//            val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            val ringtoneSound: Ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
//            ringtoneSound.play()

        }

        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                val appIntent = Intent(context, TestActivity::class.java)
                appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_FROM_BACKGROUND or
                        Intent.FLAG_INCLUDE_STOPPED_PACKAGES or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                appIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
//                appIntent.flags = Intent.FLAG_FROM_BACKGROUND
//                appIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                context.startActivity(appIntent)
                //context.sendBroadcast(appIntent)
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                Log.d("inBackground", "onReceive: connected to power")
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                val appIntent = Intent(context, Finish::class.java)
                appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                appIntent.flags = Intent.FLAG_FROM_BACKGROUND
//                appIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                //context.startActivity(appIntent)
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
                Log.d("inBackground", "onReceive: DiscConnected to power")
            }
        }

    }

}
