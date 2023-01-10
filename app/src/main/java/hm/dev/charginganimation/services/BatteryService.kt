package hm.dev.charginganimation.services

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import hm.dev.charginganimation.R
import hm.dev.charginganimation.ui.MainActivity
import hm.dev.charginganimation.ui.TestActivity
import java.util.concurrent.TimeUnit


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
        Log.d("serviceRunning", "onStartCommand: serviceRunning")
        receiverThread()


        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
//        startForeground(9999, Notification())
//        Log.d("serviceCreated", "onCreate: serviceCreated")
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




    fun receiverThread(){
        try {
            Thread.sleep(4000)
            val receiver = Intent(this, BatteryLevelReceiver::class.java)
            sendBroadcast(receiver)
            Log.d("receiverThreadRunning", "receiverThread: Thread running")
        }catch (e:InterruptedException){
            Log.d("serviceInterrupted", "receiverThread: ${e.message}")
        }
    }


}
