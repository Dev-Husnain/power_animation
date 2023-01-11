package hm.dev.charginganimation

import android.app.Application
import android.util.Log
import androidx.work.*
import hm.dev.charginganimation.services.MyWorker
import java.util.concurrent.TimeUnit

class MyApp: Application(), Configuration.Provider {
        override fun getWorkManagerConfiguration() =
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build()
    }




