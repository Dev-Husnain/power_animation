package hm.dev.charginganimation.services

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters


class MyWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        // Perform background task here

        Log.d("MyWorker", "Background task started")
        //applicationContext.startService(Intent(applicationContext, BatteryService::class.java))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context.applicationContext, Intent(context, BatteryService::class.java))
        } else {
            context.startService(Intent(applicationContext, BatteryService::class.java))
        }
        return Result.success()
    }

}

