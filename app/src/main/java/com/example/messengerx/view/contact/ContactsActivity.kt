package com.example.messengerx.view.contact

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.example.messengerx.ui.theme.ThemeMessengerX

class ContactsActivity : ComponentActivity() {

    private val viewModel: ContactsViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.loadContacts()
            } else {
                Toast.makeText(this, "Доступ к контактам отклонен", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemeMessengerX {
                LaunchedEffect(Unit) {
                    checkAndRequestPermission()
                }
                ContactsScreen(viewModel)
            }
        }
    }

    private fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                    PackageManager.PERMISSION_GRANTED -> {
                viewModel.loadContacts()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                Toast.makeText(this, "Нужно разрешение для чтения контактов", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }
}
