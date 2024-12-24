package com.example.messengerx.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // Contacts API
    @Headers("Accept: application/json")
    @GET("contacts.json")
    suspend fun getContacts(): Map<String, ContactResponse>

    @Headers("Accept: application/json")
    @POST("contacts.json")
    suspend fun addContact(@Body request: ContactRequest): Map<String, String>

    @Headers("Accept: application/json")
    @PUT("contacts/{id}.json")
    suspend fun updateContact(
        @Path("id") id: String,
        @Body request: ContactRequest
    ): Map<String, String>

    @Headers("Accept: application/json")
    @DELETE("contacts/{id}.json")
    suspend fun deleteContact(@Path("id") id: String): Map<String, String>

    // Chats API
    @Headers("Accept: application/json")
    @GET("chats")
    suspend fun getChats(): FirestoreResponse<ChatResponse>

    @Headers("Accept: application/json")
    @POST("chats")
    suspend fun createChat(@Body request: ChatRequest): FirestoreDocumentResponse

    @Headers("Accept: application/json")
    @GET("chats/{chatId}/messages")
    suspend fun getMessages(@Path("chatId") chatId: String): FirestoreResponse<MessageResponse>

    @Headers("Accept: application/json")
    @POST("chats/{chatId}/messages")
    suspend fun sendMessage(@Path("chatId") chatId: String, @Body message: MessageRequest)

    // Stories API
    @Headers("Accept: application/json")
    @GET("stories/{userId}/userStories.json")
    suspend fun getUserStories(@Path("userId") userId: String): Map<String, Story>

    @Headers("Accept: application/json")
    @POST("stories/{userId}/userStories.json")
    suspend fun addStory(@Path("userId") userId: String, @Body story: Story)

    @GET("stories.json")
    suspend fun getAllStories(): Map<String, Story>

    // Data Classes
    data class Story(
        val id: String,
        val imageUrl: String,
        val timestamp: Long,
        val userId: String
    )

    data class ChatItem(
        val id: String,
        val name: String
    )

    data class ChatRequest(
        val participants: List<String>,
        val lastMessage: String,
        val timestamp: Long
    )

    data class ChatResponse(
        val name: FieldValue,
        val timestamp: FieldValue,
        val participants: List<FieldValue> = emptyList()
    )

    data class MessageRequest(
        val fields: Map<String, FieldValue>
    )

    data class MessageResponse(
        val senderId: FieldValue,
        val message: FieldValue,
        val timestamp: FieldValue
    )

    // Firestore-specific data classes
    data class FirestoreResponse<T>(
        val documents: List<Document<T>>
    )

    data class FirestoreDocumentResponse(
        val name: String
    )

    data class Document<T>(
        val name: String,
        val fields: T
    )

    data class FieldValue(
        val stringValue: String? = null,
        val integerValue: String? = null
    )
}
