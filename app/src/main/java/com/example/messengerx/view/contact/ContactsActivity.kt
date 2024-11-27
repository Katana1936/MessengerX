package com.example.messengerx.view.contact

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
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
                ContactsScreen(viewModel)
            }
        }

        lifecycleScope.launch {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }
}

@Composable
fun ContactsScreen(viewModel: ContactsViewModel) {
    val contacts by viewModel.contactsFlow.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = true)

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else if (contacts.isEmpty()) {
        Text(text = "Контакты не найдены", modifier = Modifier.fillMaxSize())
    } else {
        LazyColumn {
            items(contacts) { contact ->
                ContactsItemCard(contact = contact)
            }
        }
    }
}
