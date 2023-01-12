package hm.dev.charginganimation.services

import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log.d
import androidx.core.app.NotificationCompat
import hm.dev.charginganimation.R
import hm.dev.charginganimation.ui.MainActivity
import hm.dev.charginganimation.ui.TestActivity


class BatteryService : Service() {

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Do your foreground service work here


        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        }
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Charging Animation is running")
            .setContentText("Go to app settings to stop the service")
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(101, notification)
        receiverThread()
        d("serviceRunning", "onStartCommand: serviceRunning")


        return START_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onCreate() {
        super.onCreate()
//        startForeground(9999, Notification())
        d("serviceCreated", "onCreate: serviceCreated")
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


    fun receiverThread() {
        try {
            Thread.sleep(4000)
            val receiver = BatteryLevelReceiver()
            val filter = IntentFilter()
            val filterBoot = IntentFilter()
            filter.addAction(Intent.ACTION_POWER_CONNECTED)
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
            filter.addAction(Intent.ACTION_BATTERY_CHANGED)
            filterBoot.addAction(Intent.ACTION_BOOT_COMPLETED)
            registerReceiver(receiver, filter)
            d("receiverThreadRunning", "receiverThread: Thread running")

        } catch (e: InterruptedException) {
            d("serviceInterrupted", "receiverThread: ${e.message}")
        }
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        val intent = Intent(this, TestActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

}
