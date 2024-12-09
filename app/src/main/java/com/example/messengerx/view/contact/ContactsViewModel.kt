package com.example.messengerx.view.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class ContactsViewModel(private val apiService: ApiService) : ViewModel() {
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
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadContacts() {
        viewModelScope.launch {
            try {
                val response = apiService.getContacts().awaitResponse()
                if (response.isSuccessful) {
                    val contactsList = response.body()?.map { (id, contact) ->
                        ContactResponse(id, contact.name, contact.phone)
                    } ?: emptyList()
                    _contacts.value = contactsList
                }
            } catch (e: Exception) {
                println("Ошибка загрузки контактов: ${e.localizedMessage}")
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
