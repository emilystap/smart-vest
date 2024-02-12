package com.example.smartvest.ui.screens

import android.Manifest
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.smartvest.R
import com.example.smartvest.ui.AppScreen
import com.example.smartvest.ui.TopAppBar
import com.example.smartvest.ui.theme.SmartVestTheme
import com.example.smartvest.ui.viewmodels.HomeViewModel
import com.example.smartvest.util.PermissionUtil
import com.example.smartvest.util.services.BleService

private const val TAG = "HomeScreen"
private lateinit var viewModel: HomeViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    title: String? = null
) {
    viewModel = HomeViewModel(LocalContext.current as Application)  /* TODO: Figure out if this is valid */

    SmartVestTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopAppBar(navController, title, canReturn = false) },
            floatingActionButton = {
                SendFab()  // floating action button for manually sending SMS
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                ConnectionStatus()
            }
        }
    }
}

/* TODO: add buttons to start tracking, refresh connection */
@Composable
private fun ConnectionStatus(modifier: Modifier = Modifier) {
    val connected = viewModel.uiState.value.connected

    val blePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (PermissionUtil.checkPermissionRequestResults(it))
            viewModel.refreshBleService()
        else
            Log.w(TAG, "Permission check returned false")
    }

    Row(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "Status: "
        )
        Text(
            text = if (connected) "Connected" else "Disconnected",
            color = (
                    if (connected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                    ),
            modifier = modifier.weight(1f)
        )
        FilledTonalButton(
            onClick = {
                PermissionUtil.checkPermissions(
                    blePermissionLauncher,
                    BleService.permissions
                )
            }
        ) {
            Text("Refresh")
        }
    }
}

@Composable
private fun SendFab() {
    var openSMSAlertDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (PermissionUtil.checkPermissionRequestResults(it))
            openSMSAlertDialog = true
    }

    val permissions = arrayOf(Manifest.permission.SEND_SMS)

    FloatingActionButton(
        onClick = {
            PermissionUtil.checkPermissions(
                permissionLauncher,
                permissions
            )
        },
        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
    ) {
        Icon(Icons.AutoMirrored.Filled.Send, "Send SMS")
    }

    if (openSMSAlertDialog) {
        SMSAlertDialog(
            onDismiss = { openSMSAlertDialog = false },
            onConfirm = {
                openSMSAlertDialog = false

                /* TODO: Implement SMS sending logic */
                Log.d("SMS Alert Dialog", "Alert Confirmed")
            }
        )
    }
}

@Composable
private fun SMSAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        icon = { Icon(Icons.Default.Warning, contentDescription = "Warning") },
        title = { Text(text = stringResource(id = R.string.sms_alert_dialog_title)) },  /* TODO: add check for location disabled */
        text = { Text(text = stringResource(id = R.string.sms_alert_dialog_text)) },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onConfirm() }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    HomeScreen(
        navController = rememberNavController(),
        title = AppScreen.Home.route
    )
}