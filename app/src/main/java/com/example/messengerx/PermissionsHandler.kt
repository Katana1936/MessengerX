package com.example.messengerx

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun PermissionsHandler(
    permissions: List<String>,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val permissionStatus = permissions.associateWith { permission ->
        remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            )
        }
    }
    val allPermissionsGranted = permissionStatus.values.all { it.value }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        results.forEach { (permission, isGranted) ->
            permissionStatus[permission]?.value = isGranted
        }
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsToRequest.isNotEmpty()) {
            launcher.launch(permissionsToRequest.toTypedArray())
        }
    }

    if (allPermissionsGranted) {
        content()
    } else {
        PermissionDeniedDialog(onRequestPermission = {
            launcher.launch(permissions.toTypedArray())
        })
    }
}

@Composable
fun PermissionDeniedDialog(onRequestPermission: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Необходимы разрешения") },
        text = { Text("Для использования камеры и галереи приложению нужны соответствующие разрешения.") },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text("Разрешить")
            }
        },
        dismissButton = {
            TextButton(onClick = { /* Опциональная логика, например, закрытие приложения */ }) {
                Text("Закрыть")
            }
        }
    )
}