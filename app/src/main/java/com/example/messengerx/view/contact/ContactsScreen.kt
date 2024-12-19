package com.example.messengerx.view.contact

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ContactsScreen(viewModel: ContactsViewModel, activity: ComponentActivity) {
    val contacts by viewModel.filteredContacts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Создаем помощник для обработки разрешений
    val permissionHelper = remember {
        ContactPermissionHelper(activity) {
            coroutineScope.launch {
                viewModel.loadContacts() // Загружаем контакты после получения разрешения
            }
        }
    }

    LaunchedEffect(Unit) {
        // Проверяем разрешения при загрузке экрана
        permissionHelper.checkAndRequestPermission()
    }

    Scaffold(
        topBar = {
            Text("Контакты", modifier = Modifier.padding(16.dp))
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (contacts.isEmpty()) {
                Text(
                    text = "Контакты не найдены",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(contacts, key = { it.id }) { contact ->
                        ContactsItemCard(contact = contact) {
                            // Логика для нажатия на контакт (например, открыть детальную информацию)
                        }
                    }
                }
            }
        }
    }
}
