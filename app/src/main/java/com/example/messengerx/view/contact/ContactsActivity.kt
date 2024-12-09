package com.example.messengerx.view.contact

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.messengerx.api.RetrofitClient
import com.example.messengerx.ui.theme.ThemeMessengerX

class ContactsActivity : ComponentActivity() {
    private lateinit var viewModel: ContactsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.getInstance()
        viewModel = ViewModelProvider(this, ContactsViewModelFactory(apiService))[ContactsViewModel::class.java]

        setContent {
            ThemeMessengerX {
                ContactsScreen(viewModel = viewModel) { contactId ->
                    println("Выбран контакт с ID: $contactId")
                }
            }
        }
    }
}
