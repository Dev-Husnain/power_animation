package hm.dev.charginganimation.utils

import androidx.lifecycle.MutableLiveData

class MyConstants {
    companion object{
        val batteryLevel=MutableLiveData<Int>()
        val batteryTypeUSB=MutableLiveData<Boolean>()
        val batteryTypeAC=MutableLiveData<Boolean>()
        val isConnected=MutableLiveData<Boolean>()

        const val REQUEST_NOTIFICATION_CODE = 101
        const val REQUEST_DISABLE_KEYGUARD = 1
        const val REQUEST_CODE_WRITE_SETTINGS = 333
    }

}