package com.example.messengerx.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class ContactPermissionHelper(
    private val activity: ComponentActivity,
    private val onPermissionGranted: () -> Unit
) {
    fun checkAndRequestPermission() {
        val context = activity.applicationContext
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                onPermissionGranted()
            }

            else -> {
                activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (isGranted) {
                        onPermissionGranted()
                    } else {
                        Toast.makeText(context, "Доступ к контактам отклонен", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }
}
