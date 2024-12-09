package com.example.messengerx.view.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

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
                val response = RetrofitClient.apiService.getContacts().awaitResponse()
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let { contactsMap ->
                        val contacts = contactsMap.map { (id, value) ->
                            ContactResponse(
                                id = id,
                                name = value.name,
                                phone = value.phone
                            )
                        }
                        _contacts.value = contacts
                    }
                } else {
                    println("Ошибка загрузки контактов: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Ошибка загрузки контактов: ${e.message}")
            }
        }
    }

    fun addContact(name: String, phone: String) {
        viewModelScope.launch {
            try {
                val contactRequest = ContactRequest(name = name, phone = phone)
                val response = RetrofitClient.apiService.addContact(contactRequest).awaitResponse()
                if (response.isSuccessful) {
                    loadContacts()
                } else {
                    println("Ошибка добавления контакта: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Ошибка добавления контакта: ${e.message}")
            }
        }
    }

    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteContact(contactId).awaitResponse()
                if (response.isSuccessful) {
                    loadContacts()
                } else {
                    println("Ошибка удаления контакта: ${response.errorBody()?.string()}")
                }
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
