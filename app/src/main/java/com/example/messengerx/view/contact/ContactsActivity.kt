package com.example.messengerx.view.contact

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
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
                ContactsList(viewModel)
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }
}


@Composable
fun ContactsList(viewModel: ContactsViewModel) {
    val contacts by viewModel.contacts.observeAsState(emptyList())
    LazyColumn {
        items(contacts) { contact ->
            ContactsItemCard(contact = contact)
        }
    }
}

