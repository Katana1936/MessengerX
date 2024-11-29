package com.example.messengerx.view.contact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.messengerx.R
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(viewModel: ContactsViewModel) {
    val contacts by viewModel.filteredContacts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Контакты") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Добавить действие сортировки */ }) {
                        Icon(Icons.Filled.Sort, contentDescription = "Сортировка")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Добавить действие добавления контакта */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Добавить"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Поиск") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            LazyColumn {
                items(contacts) { contact ->
                    ContactsItemCard(contact = contact)
                }
            }
        }
    }
}