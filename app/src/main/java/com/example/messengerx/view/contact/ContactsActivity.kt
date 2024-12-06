package com.example.messengerx.view.contact

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.example.messengerx.ui.theme.ThemeMessengerX

class ContactsActivity : ComponentActivity() {

    private val viewModel: ContactsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация хелпера для работы с разрешениями
        val permissionHelper = ContactPermissionHelper(
            context = this,
            onPermissionGranted = { viewModel.loadContacts() }
        )

        setContent {
            ThemeMessengerX {
                LaunchedEffect(Unit) {
                    permissionHelper.checkAndRequestPermission()
                }
                ContactsScreen(viewModel)
            }
        }
    }
}
