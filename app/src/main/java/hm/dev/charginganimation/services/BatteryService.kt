package hm.dev.charginganimation.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log.d
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import hm.dev.charginganimation.R
import hm.dev.charginganimation.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BatteryService : Service() {

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"

    }

    private lateinit var wakeLock: PowerManager.WakeLock


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Do your foreground service work here
        val flagsMutabilityIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT

        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, flagsMutabilityIntent)

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Charging Animation is running")
            .setContentText("Go to app settings to stop the service")
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .setVisibility(VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .addAction(R.drawable.ic_baseline_notifications_24, "View App", pendingIntent)
            .build()

        startForeground(101, notification)
        startBroadCastReceiver()
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


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH

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

    override fun onDestroy() {
        super.onDestroy()
        restartService()
    }

    private fun startBroadCastReceiver() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BatteryService::lock")
        wakeLock.acquire(5000)
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                launch(Dispatchers.IO) {
                    val receiver = BatteryLevelReceiver()
                    val filter = IntentFilter()
                    val filterBoot = IntentFilter()
                    filter.addAction(Intent.ACTION_POWER_CONNECTED)
                    filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
                    filter.addAction(Intent.ACTION_BATTERY_CHANGED)
                    filterBoot.addAction(Intent.ACTION_BOOT_COMPLETED)
                    registerReceiver(receiver, filter)
                    d("receiverThreadRunning", "receiverThread: Thread running")
                }
                delay(4000)
            }
        }

    }


    override fun onTaskRemoved(rootIntent: Intent?) {
//        val intent = Intent(this, TestActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
        restartService()

    }

    private fun restartService() {
        val serviceIntent = Intent(applicationContext, BatteryService::class.java).also {
            it.setPackage(packageName)
        }

        val flagsMutabilityIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT

        val restartServiceIntent: PendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) PendingIntent.getForegroundService(
                applicationContext,
                101,
                serviceIntent,
                flagsMutabilityIntent
            )
            else PendingIntent.getService(
                applicationContext,
                101,
                serviceIntent,
                flagsMutabilityIntent
            )


        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        val triggerTime = SystemClock.elapsedRealtime() + 5000 // 5 seconds

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                restartServiceIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                restartServiceIntent
            )
        }

    }

}
