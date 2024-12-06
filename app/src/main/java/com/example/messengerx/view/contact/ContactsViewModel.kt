package com.example.messengerx.view.contact

import android.app.Application
import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val filteredContacts = combine(_contacts, _searchQuery) { contacts, query ->
        if (query.isEmpty()) {
            contacts
        } else {
            contacts.filter {
                it.name.contains(query, ignoreCase = true) || it.phone.contains(query)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val contacts = fetchContacts()
                _contacts.value = contacts
            } catch (e: Exception) {
                println("Ошибка загрузки контактов: ${e.message}")
            }
        }
    }

    private fun fetchContacts(): List<Contact> {
        val resolver: ContentResolver = getApplication<Application>().contentResolver
        val contactList = mutableListOf<Contact>()
        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phone = it.getString(numberIndex)
                val id = phone.hashCode().toString() // Используем хэш от номера как уникальный ID
                contactList.add(Contact(id = id, name = name, phone = phone))
            }
        }
        return contactList
    }

    fun addContact(name: String, phone: String) {
        viewModelScope.launch {
            val id = phone.hashCode().toString() // Генерируем уникальный ID для нового контакта
            val updatedContacts = _contacts.value.toMutableList()
            updatedContacts.add(Contact(id = id, name = name, phone = phone))
            _contacts.value = updatedContacts
        }
    }
}



data class ContactRequest(
    val name: String,
    val phone: String
)

data class ContactResponse(
    val id: String,
    val name: String,
    val phone: String
)

