package com.example.messengerx.view.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _chatList = MutableStateFlow<List<ChatItem>>(emptyList())
    val chatList: StateFlow<List<ChatItem>> = _chatList

    init {
        loadChats()
    }

    // Загрузка всех чатов текущего пользователя
    private fun loadChats() {
        db.collection("chats")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Ошибка чтения чатов: ${e.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val chats = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ChatItem::class.java)
                    }
                    _chatList.value = chats
                }
            }
    }

    // Проверка на существование чата или его создание
    fun createOrLoadChat(userId: String, contactId: String, onChatLoaded: (String) -> Unit) {
        db.collection("chats")
            .whereArrayContains("participants", userId)
            .get()
            .addOnSuccessListener { result ->
                val chat = result.documents.firstOrNull { doc ->
                    val participants = doc.get("participants") as? List<*>
                    participants?.contains(contactId) == true
                }

                if (chat != null) {
                    // Чат существует
                    onChatLoaded(chat.id)
                } else {
                    // Создаем новый чат
                    val newChat = hashMapOf(
                        "participants" to listOf(userId, contactId),
                        "lastMessage" to "",
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("chats")
                        .add(newChat)
                        .addOnSuccessListener { documentReference ->
                            onChatLoaded(documentReference.id)
                        }
                        .addOnFailureListener { e ->
                            println("Ошибка создания чата: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                println("Ошибка поиска чата: ${e.message}")
            }
    }
}
