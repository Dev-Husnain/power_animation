package hm.dev.charginganimation.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.BatteryManager
import android.util.Log
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import hm.dev.charginganimation.ui.TestActivity
import hm.dev.charginganimation.utils.MyConstants


class BatteryLevelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryRemaining = level / scale.toFloat() * 100


            val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL


            val chargePlug: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

            MyConstants.batteryLevel.value = batteryRemaining.toInt()
            MyConstants.isConnected.value = isCharging
            MyConstants.batteryTypeUSB.value = usbCharge
            MyConstants.batteryTypeAC.value = acCharge
            Toast.makeText(context, "Broadcast running", Toast.LENGTH_SHORT).show()


            val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtoneSound: Ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
            ringtoneSound.play()

        }

        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                val appIntent = Intent(context, TestActivity::class.java)
                appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                appIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                context.startActivity(appIntent)

                //context.sendBroadcast(appIntent)
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                Log.d("inBackground", "onReceive: connected to power")
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
                Log.d("inBackground", "onReceive: DiscConnected to power")
            }
        }

    }


}