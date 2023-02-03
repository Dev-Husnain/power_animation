package hm.dev.charginganimation.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.BackoffPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import hm.dev.charginganimation.R
import hm.dev.charginganimation.databinding.ActivityMainBinding
import hm.dev.charginganimation.services.BatteryLevelReceiver
import hm.dev.charginganimation.services.BatteryService
import hm.dev.charginganimation.services.BootReceiver
import hm.dev.charginganimation.services.MyWorker
import hm.dev.charginganimation.utils.MyConstants
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var rebootReceiver: BootReceiver

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(applicationContext, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Permission not granted", Toast.LENGTH_SHORT)
                    .show()
            }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //workManager()

        //getAutoStartPermission()
        // xiaomiBGPermission()


        //oppoAutoPermission()

        startBroadCastReceiver()
        doNotKillApp()
        getDeviceInfo()
        setAlarmToStartService()


        //notificationsPermission()


    }

    @SuppressLint("HardwareIds")
    private fun getDeviceInfo() {
        val brandName = Build.BRAND
        val iD = Build.ID
        val deviceModel = Build.MODEL
        val deviceId = Settings.Secure.getString(
            applicationContext.contentResolver, Settings.Secure.ANDROID_ID
        )
        val sDk = Build.VERSION.SDK_INT
        val manufacturer = Build.MANUFACTURER
        val user = Build.USER
        val type = Build.TYPE
        val base = Build.VERSION_CODES.BASE
        val incremental = Build.VERSION.INCREMENTAL
        val board = Build.BOARD
        val host = Build.HOST
        val display = Build.DISPLAY
        val hardware = Build.HARDWARE
        val product = Build.PRODUCT
        val cPU_aBI = Build.SUPPORTED_ABIS
        val fingerPrint = Build.FINGERPRINT
        val version = Build.VERSION.RELEASE
        Log.d(
            "getDeviceInfo",
            "getDeviceInfo: Brand: $brandName, IDs: $iD/$deviceId, $deviceModel, " + "SDK: $sDk,Manufacturer: $manufacturer,User: $user,Type: $type,Base: $base,incremental: $incremental, version: $version  "
        )

    }

    private fun notificationsPermission() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP_MR1")
        }
        startActivity(intent)


    }


    private fun setAlarmToStartService() {
        val pendingIntent2 =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT
        val serviceIntent = Intent(this, BatteryService::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(this, 0, serviceIntent, pendingIntent2)
        } else {
            PendingIntent.getService(this, 0, serviceIntent, pendingIntent2)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 60000, pendingIntent
        ) // repeat every 5 seconds
        Log.d(
            "workManager",
            "setAlarmToStartService: Alarm scheduled at ${calendar.timeInMillis + 5000}"
        )

    }

    private fun doNotKillApp() {
        val component = ComponentName(this, BatteryLevelReceiver::class.java)
        val pm: PackageManager = applicationContext.packageManager
        pm.setComponentEnabledSetting(
            component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )


        val componentService = ComponentName(this, BatteryService::class.java)
        val pmService: PackageManager = applicationContext.packageManager
        pmService.setComponentEnabledSetting(
            componentService,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }


    private fun workManager() {
        val uniqueWorkName = "StartMyServiceViaWorker"
//        val constraints= Constraints.Builder()
//            .setRequiresCharging(true)
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()

//        val repeatingWork = PeriodicWorkRequestBuilder<MyWorker>(5, TimeUnit.SECONDS)
        val repeatingWork = PeriodicWorkRequest.Builder(
            MyWorker::class.java, 15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES
        ).setInitialDelay(5, TimeUnit.SECONDS)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.SECONDS).addTag(uniqueWorkName)
            .build()

        WorkManager.getInstance(this).enqueue(repeatingWork)
        //WorkManager.getInstance(this).enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, repeatingWork)

        val workInfo =
            WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData(uniqueWorkName)
        workInfo.observe(this) {
            if (it != null && it.isNotEmpty()) {
                val state = it[0].state
                if (state.isFinished) {
                    Log.d("workManager", "onCreate: work manager finished")
                } else {
                    Log.d("workManager", "onCreate: work manager running")
                }
            } else {
                Log.d("workManager", "onCreate: work manager is empty")
            }
        }


    }

    private fun oppoAutoPermission() {
        if (Build.MANUFACTURER.equals("OPPO", ignoreCase = true)) {
            val packageName = packageManager.getLaunchIntentForPackage(packageName)?.`package`
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }

    private fun getAutoStartPermission() {
        val powerManagerIntents = arrayOf(
            Intent().setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            ), Intent().setComponent(
                ComponentName(
                    "com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"
                )
            ), Intent().setComponent(
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
            ), Intent().setComponent(
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            ),

            Intent().setComponent(
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
            ),

            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            ), Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.startupapp.StartupAppListActivity"
                )
            ), Intent().setComponent(
                ComponentName(
                    "com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"
                )
            ), Intent().setComponent(
                ComponentName(
                    "com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity"
                )
            ),


            Intent().setComponent(
                ComponentName(
                    "com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                )
            ), Intent().setComponent(
                ComponentName(
                    "com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
                )
            ), Intent().setComponent(
                ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            ), Intent().setComponent(
                ComponentName(
                    "com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"
                )
            ), Intent().setComponent(
                ComponentName(
                    "com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity"
                )
            ), Intent().setComponent(
                ComponentName(
                    "com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"

                )
            )
        )



        for (intent in powerManagerIntents) if (packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) != null
        ) {
            // show dialog to ask user action
            startActivity(intent)
            break
        }
    }


    private fun startBroadCastReceiver() {
        val filter = IntentFilter()
        val filterBoot = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filterBoot.addAction(Intent.ACTION_BOOT_COMPLETED)


        rebootReceiver = BootReceiver()
        this.registerReceiver(rebootReceiver, filterBoot)


        MyConstants.batteryLevel.observe(this@MainActivity) {
            binding.batteryRemain.text = MyConstants.batteryLevel.value.toString()
        }

        MyConstants.batteryTypeUSB.observe(this) {
            if (MyConstants.batteryTypeUSB.value == true) {
                binding.usbType.text = getString(R.string.usb)
            }
        }
        MyConstants.batteryTypeAC.observe(this) {
            if (MyConstants.batteryTypeAC.value == true) {
                binding.aCType.text = getString(R.string.ac_con)
            }

        }
        MyConstants.isConnected.observe(this) {
            if (MyConstants.isConnected.value == true) {
                binding.isConnected.text = getString(R.string.connected)
            } else {
                binding.isConnected.text = getString(R.string.not_connected)
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, Intent(this, BatteryService::class.java))
        } else {
            this.startService(Intent(applicationContext, BatteryService::class.java))
        }

    }


    private fun xiaomiBGPermission() {
        if (Build.MANUFACTURER.equals("Xiaomi", true)) {
            try {
                val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                intent.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity"
                )
                intent.putExtra("extra_pkgname", packageName)
                // this.startActivity(intent)
                permissionLauncher.launch(intent)
            } catch (e: Exception) {
                // Handle the exception, for example by showing a message to the user
                //Toast.makeText(this, "Permission editor not found", Toast.LENGTH_SHORT).show()
            }

        }
    }


}