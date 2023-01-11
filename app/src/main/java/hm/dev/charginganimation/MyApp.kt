package hm.dev.charginganimation

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val configuration = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        WorkManager.initialize(this, configuration)

    }

}