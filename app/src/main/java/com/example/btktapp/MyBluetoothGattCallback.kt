package com.example.btktapp

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat


////////////////////////////////////////////////////////////////////////////////////////////

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback


class MyBluetoothGattCallback(private val context: Context) : BluetoothGattCallback() {

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)

        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
                Log.i(TAG, "Connected to GATT server.")
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return
                }
                gatt?.discoverServices()

                val device = gatt?.device
                device?.let {
                    val intent = Intent(ACTION_DEVICE_CONNECTED)
                    intent.putExtra(EXTRA_DEVICE_NAME, device.name)
                    intent.putExtra(EXTRA_DEVICE_ADDRESS, device.address)
                    context.sendBroadcast(intent)
                }


                showNotification("Bluetooth Device Connected", "Your device is now connected.")
            }

            BluetoothProfile.STATE_DISCONNECTED -> {
                Log.i(TAG, "Disconnected from GATT server.")


                showNotification("Bluetooth Device Disconnected", "Your device is now disconnected.")

                // Start BluetoothScanService
                val serviceIntent = Intent(context, BluetoothScanService::class.java)
                context.startService(serviceIntent)
            }
        }
    }

    private fun showNotification(title: String, content: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "my_channel_id"
        val channelName = "My Channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }

    companion object {
        const val TAG = "MyBluetoothGattCallback"
        const val ACTION_DEVICE_CONNECTED = "com.example.btkt.ACTION_DEVICE_CONNECTED"
        const val EXTRA_DEVICE_NAME = "extra_device_name"
        const val EXTRA_DEVICE_ADDRESS = "extra_device_address"
    }
}
