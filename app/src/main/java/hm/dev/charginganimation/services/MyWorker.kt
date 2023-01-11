package hm.dev.charginganimation.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters


class MyWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        // Perform background task here

        Toast.makeText(applicationContext, "My Worker Running", Toast.LENGTH_SHORT).show()
        Log.d("MyWorker", "Background task started")
        //applicationContext.startService(Intent(applicationContext, BatteryService::class.java))
        return Result.success()
    }

}

