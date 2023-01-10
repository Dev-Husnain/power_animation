package hm.dev.charginganimation.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import hm.dev.charginganimation.R

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (checkOverlayPermission()){
            startActivity(Intent(this@Splash, MainActivity::class.java))
        }else{
            startActivity(Intent(this@Splash, DisplayOverApp::class.java))
        }
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }
}