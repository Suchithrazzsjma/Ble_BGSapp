package com.example.btktapp




///////////////////////////////////////////////////////////////////////////////////////
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var connectedDeviceNameTextView: TextView
    private lateinit var connectedDeviceAddressTextView: TextView
    private val deviceConnectedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == MyBluetoothGattCallback.ACTION_DEVICE_CONNECTED) {
                    val deviceName = it.getStringExtra(MyBluetoothGattCallback.EXTRA_DEVICE_NAME)
                    val deviceAddress = it.getStringExtra(MyBluetoothGattCallback.EXTRA_DEVICE_ADDRESS)
                    connectedDeviceNameTextView.text = "Connected Device Name: $deviceName"
                    connectedDeviceAddressTextView.text = "Connected Device Address: $deviceAddress"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectedDeviceNameTextView = findViewById(R.id.connectedDeviceNameTextView)
        connectedDeviceAddressTextView = findViewById(R.id.connectedDeviceAddressTextView)

        registerReceiver(
            deviceConnectedReceiver,
            IntentFilter(MyBluetoothGattCallback.ACTION_DEVICE_CONNECTED)
        )
    }

    override fun onResume() {
        super.onResume()
        startBluetoothScanService()
    }

    private fun startBluetoothScanService() {
        val serviceIntent = Intent(this, BluetoothScanService::class.java)
        startService(serviceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(deviceConnectedReceiver)
    }
}

