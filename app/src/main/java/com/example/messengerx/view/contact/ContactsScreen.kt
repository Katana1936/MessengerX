package com.example.messengerx.view.contact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.messengerx.PermissionsHandler

@Composable
fun ContactsScreen(viewModel: ContactsViewModel) {
    val contacts = viewModel.filteredContacts.collectAsState(initial = emptyList())

    PermissionsHandler(
        permissions = listOf(android.Manifest.permission.READ_CONTACTS),
        rationaleText = "Для отображения контактов необходимо разрешение.",
        onPermissionsGranted = {
            viewModel.loadContacts()
        }
    ) {
        Scaffold(
            topBar = {
                Text(
                    text = "Контакты",
                    modifier = Modifier.padding(16.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                if (contacts.value.isEmpty()) {
                    Text(
                        text = "Контакты не найдены",
                        modifier = Modifier.padding(16.dp),
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn {
                        items(contacts.value, key = { it.id }) { contact ->
                            ContactsItemCard(contact = contact) {
                                // Обработчик клика по контакту
                            }
                        }
                    }
                }
            }
        }
    }
}
