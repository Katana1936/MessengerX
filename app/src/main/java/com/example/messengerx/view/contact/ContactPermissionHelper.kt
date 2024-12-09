package com.example.messengerx.view.contact

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class ContactPermissionHelper(
    activity: ComponentActivity,
    private val onPermissionGranted: () -> Unit
) {
    private val context = activity.applicationContext

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                Toast.makeText(context, "Доступ к контактам отклонен", Toast.LENGTH_SHORT).show()
            }
        }

    fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
                    PackageManager.PERMISSION_GRANTED -> {
                onPermissionGranted()
            }
            (context as? ComponentActivity)
                ?.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) == true -> {
                Toast.makeText(context, "Нужно разрешение для чтения контактов", Toast.LENGTH_LONG).show()
                requestPermissionLauncher?.launch(Manifest.permission.READ_CONTACTS)
            }
            else -> {
                requestPermissionLauncher?.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }
}
