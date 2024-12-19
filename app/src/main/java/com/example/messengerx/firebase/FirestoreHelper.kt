package com.example.messengerx.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {
    val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getFirestore(): FirebaseFirestore = firestore

    fun initializeUserCollections(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onFailure("Пользователь не авторизован")
            return
        }

        val userId = currentUser.uid

        val batch = firestore.batch()

        // Создание документа для чатов
        val chatDoc = firestore.collection("chats").document(userId)
        batch.set(chatDoc, mapOf("participants" to listOf(userId)))

        // Создание коллекции историй
        val storyDoc = firestore.collection("stories")
            .document(userId)
            .collection("userStories")
            .document("welcome_story")
        batch.set(storyDoc, mapOf("title" to "Добро пожаловать!", "timestamp" to System.currentTimeMillis()))

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception.message ?: "Ошибка инициализации Firestore") }
    }
}

