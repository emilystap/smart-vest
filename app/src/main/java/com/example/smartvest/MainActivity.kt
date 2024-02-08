package com.example.smartvest

import android.content.Intent
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import com.example.smartvest.ui.AppNav
import com.example.smartvest.ui.theme.SmartVestTheme
import com.example.smartvest.util.services.BleService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* TODO: Use PermissionHandler */
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            0
        )

        startService(Intent(this, BleService::class.java))

        setContent {
            SmartVestTheme {
                AppNav()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, BleService::class.java))
    }
}