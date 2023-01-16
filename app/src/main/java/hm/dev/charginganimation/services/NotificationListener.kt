package hm.dev.charginganimation.services

import android.app.Notification
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class NotificationListener : NotificationListenerService() {
    override fun onListenerConnected() {
        Log.d("notifications", "onListenerConnected: Notification Listener connected")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.d("notifications", "onNotificationPosted: Notification Listener connected")

       // flashLight()
        if (sbn.packageName != "com.whatsapp") return
        val notification: Notification = sbn.notification
        val bundle: Bundle = notification.extras
        val from = bundle.getString(NotificationCompat.EXTRA_TITLE)
        val message = bundle.getString(NotificationCompat.EXTRA_TEXT)
        Log.i(
            "notifications" +
                    "", "From: $from"
        )
        Log.i(
            "notifications" +
                    "", "Message: $message"
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun flashLight(){
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0] // Use the first camera on the device
        cameraManager.setTorchMode(cameraId, true)
        Thread.sleep(500)
        cameraManager.setTorchMode(cameraId, false)
    }

}