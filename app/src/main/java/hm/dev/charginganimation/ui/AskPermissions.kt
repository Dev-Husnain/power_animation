package hm.dev.charginganimation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import hm.dev.charginganimation.databinding.ActivityDisplayOverAppBinding


class AskPermissions : AppCompatActivity() {
    private lateinit var binding: ActivityDisplayOverAppBinding


    private val displayOverAppsPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (checkOverlayPermission()) {
                requestPostNotificationPermission()
            } else {
                askPermission()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayOverAppBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if (checkOverlayPermission()) {
            requestPostNotificationPermission()
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

    private fun requestPostNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            } else {
                startNewNext()
            }
        } else {
            startNewNext()

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    startNewNext()
                } else {
                    // Permission denied
                    requestPostNotificationPermission()
                }
            }
        }
    }


}