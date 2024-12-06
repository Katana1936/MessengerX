package com.example.messengerx.view.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.messengerx.api.ApiService


class ContactsViewModel : ViewModel() {

    private val _contacts = MutableStateFlow<List<ContactResponse>>(emptyList())
    val contacts: StateFlow<List<ContactResponse>> = _contacts

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
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadContacts() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getData("contacts")
                val contacts = response.map { (id, value) ->
                    val data = value as Map<String, Any>
                    ContactResponse(
                        id = id,
                        name = data["name"] as String,
                        phone = data["phone"] as String
                    )
                }
                _contacts.value = contacts
            } catch (e: Exception) {
                println("Ошибка загрузки контактов: ${e.message}")
            }
        }
    }

    fun addContact(name: String, phone: String) {
        viewModelScope.launch {
            try {
                val contactRequest = ContactRequest(name = name, phone = phone)
                RetrofitClient.apiService.postData("contacts", contactRequest)
                loadContacts()
            } catch (e: Exception) {
                println("Ошибка добавления контакта: ${e.message}")
            }
        }
    }

    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteData("contacts", contactId)
                loadContacts()
            } catch (e: Exception) {
                println("Ошибка удаления контакта: ${e.message}")
            }
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

