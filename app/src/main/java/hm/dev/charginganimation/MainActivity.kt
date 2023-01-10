package hm.dev.charginganimation
import android.app.LauncherActivity
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hm.dev.charginganimation.databinding.ActivityMainBinding
import hm.dev.charginganimation.utils.MyConstants

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: BatteryLevelReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()





        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)


        intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES

        receiver = BatteryLevelReceiver()
         this.registerReceiver(receiver, filter)








        MyConstants.batteryLevel.observe(this@MainActivity){
            binding.batteryRemain.text=MyConstants.batteryLevel.value.toString()
        }

        MyConstants.batteryTypeUSB.observe(this){
            if (MyConstants.batteryTypeUSB.value==true)
            {
                binding.usbType.text=getString(R.string.usb)
            }
        }
        MyConstants.batteryTypeAC.observe(this){
            if (MyConstants.batteryTypeAC.value==true){
                binding.aCType.text=getString(R.string.ac_con)
            }

        }
        MyConstants.isConnected.observe(this){
            if (MyConstants.isConnected.value==true){
                binding.isConnected.text=getString(R.string.connected)
            }else{
                binding.isConnected.text=getString(R.string.not_connected)
            }

        }


        startService(Intent(applicationContext,BatteryService::class.java))

    }
}