package com.example.messengerx.view.contact

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.messengerx.ui.theme.ThemeMessengerX

class ContactsActivity : ComponentActivity() {
    private val viewModel: ContactsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemeMessengerX {
                ContactsList(viewModel = viewModel)
            }
        }


        viewModel.loadContacts()
    }
}
