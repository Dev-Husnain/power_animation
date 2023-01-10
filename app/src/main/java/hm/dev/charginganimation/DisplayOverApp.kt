package hm.dev.charginganimation

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import hm.dev.charginganimation.databinding.ActivityDisplayOverAppBinding

class DisplayOverApp : AppCompatActivity() {
    private lateinit var binding:ActivityDisplayOverAppBinding
    private val requestSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (checkOverlayPermission()) {
                startNewNext()
            }else{
                askPermission()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDisplayOverAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (checkOverlayPermission()) {
            startNewNext()
        } else {
            askPermission()
        }
    }

   private fun askPermission(){
    val myIntent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    requestSettingLauncher.launch(myIntent)
}
    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun startNewNext(){
        startActivity(Intent(this,MainActivity::class.java))
        finish()


    }
}