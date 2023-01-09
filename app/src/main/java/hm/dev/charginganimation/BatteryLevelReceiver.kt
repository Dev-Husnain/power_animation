package hm.dev.charginganimation
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.widget.Toast



 class BatteryLevelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        //auto start permission
        //run in background permission



//        if (intent.action == Intent.ACTION_BATTERY_CHANGED){
//            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
//            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
//            val batteryRemaining= level / scale.toFloat() * 100
//
//
//            val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
//            val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
//                    || status == BatteryManager.BATTERY_STATUS_FULL
//
//
//            val chargePlug: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
//            val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
//            val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
//
//            MyConstants.batteryLevel.value=batteryRemaining.toInt()
//            MyConstants.isConnected.value=isCharging
//            MyConstants.batteryTypeUSB.value=usbCharge
//            MyConstants.batteryTypeAC.value=acCharge
//
//
////            val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
////            val ringtoneSound: Ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
////            ringtoneSound.play()
//
//        }

//
        when(intent.action){
            Intent.ACTION_POWER_CONNECTED->{
                val i = Intent(context, TestActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                i.addFlags(Intent.FLAG_FROM_BACKGROUND)
                i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                context.startActivity(i)
                val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtoneSound: Ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
            ringtoneSound.play()
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
            }
            Intent.ACTION_POWER_DISCONNECTED->{
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
            }
        }

    }





}
