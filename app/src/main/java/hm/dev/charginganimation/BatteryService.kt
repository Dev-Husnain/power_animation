package hm.dev.charginganimation

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat


class BatteryService() : Service() {

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int     {
        // Do your foreground service work here



        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
        }
        else { PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT) }
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Charging Animation is running")
            .setContentText("Go to app settings to stop the service")
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        receiverThread()




        return START_STICKY
    }

      fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    fun receiverThread(){
        try {
            Thread.sleep(4000)
        }catch (e:InterruptedException){
            Log.d("serviceInterrupted", "receiverThread: ${e.message}")
        }finally {
            val receiver = Intent(this, BatteryLevelReceiver::class.java)
            sendBroadcast(receiver)
//            val intent=Intent(this,TestActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
            Log.d("serviceCalled", "receiverThread: called")
        }
    }


}
