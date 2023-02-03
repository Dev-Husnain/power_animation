package hm.dev.charginganimation.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import hm.dev.charginganimation.databinding.ActivityDisplayOverAppBinding
import hm.dev.charginganimation.utils.MyConstants.Companion.REQUEST_DISABLE_KEYGUARD
import hm.dev.charginganimation.utils.MyConstants.Companion.REQUEST_NOTIFICATION_CODE


class AskPermissions : AppCompatActivity() {
    private lateinit var binding: ActivityDisplayOverAppBinding


    private val displayOverAppsPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (checkOverlayPermission()) {
                askPostNotificationPermission()
            } else {
                askPermission()
            }
        }

    private val batteryOptimizationResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            startNewNext()
        } else {
            askBatteryOptimizationPermission(this)
            // Permission is not granted
            // Provide an alternative solution or a fallback option
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayOverAppBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if (checkOverlayPermission()) {
            askPostNotificationPermission()
        } else {
            askPermission()
        }


    }

    private fun askPermission() {
        val myIntent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION
        )
        displayOverAppsPermission.launch(myIntent)
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }


    private fun startNewNext() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun askPostNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_CODE
                )
            } else {
                askBatteryOptimizationPermission(this)
            }
        } else {
            askBatteryOptimizationPermission(this)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_NOTIFICATION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    startNewNext()
                } else {
                    // Permission denied
                    askPostNotificationPermission()
                }
            }


            REQUEST_DISABLE_KEYGUARD -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    startNewNext()
                } else {
                    askDisableKeyguardPermission(this@AskPermissions)
                    // Permission is denied
                    // Provide an alternative solution or a fallback option
                }
            }
        }
    }


    private fun askBatteryOptimizationPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = context.packageName
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (pm.isIgnoringBatteryOptimizations(packageName)) {
                startNewNext()
                //Permission is granted
            } else {
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                //context.startActivity(intent)
                batteryOptimizationResult.launch(intent)
            }
        } else {
            startNewNext()
        }


    }

    private fun askDisableKeyguardPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.DISABLE_KEYGUARD
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.DISABLE_KEYGUARD),
                REQUEST_DISABLE_KEYGUARD
            )
        } else {
            startNewNext()
        }
    }

    fun xiaomiBGPermission(){
        if (Build.MANUFACTURER.equals("Xiaomi",true)) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
            intent.putExtra("extra_pkgname", packageName)
            startActivity(intent)
        }
    }


}