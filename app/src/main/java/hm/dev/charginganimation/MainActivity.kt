package hm.dev.charginganimation
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import hm.dev.charginganimation.databinding.ActivityMainBinding
import hm.dev.charginganimation.utils.MyConstants

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: BatteryLevelReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)

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
                Toast.makeText(applicationContext, MyConstants.batteryTypeAC.value.toString(), Toast.LENGTH_SHORT).show()

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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}