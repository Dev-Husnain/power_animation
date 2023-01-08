package hm.dev.charginganimation.utils

import androidx.lifecycle.MutableLiveData

class MyConstants {
    companion object{
        val batteryLevel=MutableLiveData<Int>()
        val batteryTypeUSB=MutableLiveData<Boolean>()
        val batteryTypeAC=MutableLiveData<Boolean>()
        val isConnected=MutableLiveData<Boolean>()
    }

}