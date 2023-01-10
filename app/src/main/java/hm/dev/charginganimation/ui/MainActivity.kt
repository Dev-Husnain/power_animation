package hm.dev.charginganimation.ui

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import hm.dev.charginganimation.R
import hm.dev.charginganimation.databinding.ActivityMainBinding
import hm.dev.charginganimation.services.BatteryLevelReceiver
import hm.dev.charginganimation.services.BatteryService
import hm.dev.charginganimation.utils.MyConstants


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: BatteryLevelReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        startBroadCastReceiver()
        getAutoStartPermission()

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)




    }

    private fun getAutoStartPermission() {
        val POWERMANAGER_INTENTS = arrayOf(Intent().setComponent(ComponentName("com.miui.securitycenter",
            "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            Intent().setComponent(ComponentName("com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AutobootManageActivity")),
            Intent().setComponent(ComponentName("com.huawei.systemmanager",
                "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
            Intent().setComponent(
                ComponentName("com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity")),

            Intent().setComponent(ComponentName("com.huawei.systemmanager",
                "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),

            Intent().setComponent(
                ComponentName("com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity") ),
            Intent().setComponent(ComponentName("com.coloros.safecenter",
                "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            Intent().setComponent(
                ComponentName("com.oppo.safe",
                    "com.oppo.safe.permission.startup.StartupAppListActivity")),
            Intent().setComponent(
                ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            Intent().setComponent(ComponentName("com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            Intent().setComponent(ComponentName("com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            Intent().setComponent(ComponentName("com.samsung.android.lool",
                "com.samsung.android.sm.ui.battery.BatteryActivity")),
            Intent().setComponent(ComponentName("com.htc.pitroad",
                "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            Intent().setComponent(ComponentName("com.asus.mobilemanager",
                "com.asus.mobilemanager.MainActivity"

            )
            )
        )



        for (intent in POWERMANAGER_INTENTS)
            if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            // show dialog to ask user action
            showAlert(intent)
            break
        }
    }

    private fun showAlert(intent: Intent) {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("On this device you must allow us to run services in background")
            .setPositiveButton("Give Permission") { _, _ ->
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ -> finish() }
        dialog.show()


    }

    private fun startBroadCastReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)


        receiver = BatteryLevelReceiver()
        this.registerReceiver(receiver, filter)


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

}