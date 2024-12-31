package com.example.messengerx.api

import com.example.messengerx.view.contact.ContactRequest
import com.example.messengerx.view.contact.ContactResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

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

    @GET("chats")
    suspend fun getChats(): FirestoreResponse<ChatResponse>

    @POST("chats")
    suspend fun createChat(@Body request: FirestoreDocumentRequest<ChatRequest>): FirestoreDocumentResponse

    @GET("chats/{chatId}/messages")
    suspend fun getMessages(@Path("chatId") chatId: String): FirestoreResponse<MessageResponse>

    @POST("chats/{chatId}/messages")
    suspend fun sendMessage(
        @Path("chatId") chatId: String,
        @Body request: FirestoreDocumentRequest<MessageRequest>
    )

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

    data class ChatRequest(
        val participants: List<FieldValue>,
        val lastMessage: FieldValue,
        val timestamp: FieldValue
    )

    data class MessageRequest(
        val senderId: FieldValue,
        val message: FieldValue,
        val timestamp: FieldValue
    )

    data class ChatResponse(
        val participants: List<FieldValue>,
        val lastMessage: FieldValue,
        val timestamp: FieldValue
    )

    data class ChatItem(
        val id: String,
        val name: String
    )

    data class MessageResponse(
        val senderId: FieldValue,
        val message: FieldValue,
        val timestamp: FieldValue
    )

    data class FieldValue(
        val stringValue: String? = null,
        val timestampValue: String? = null
    )

    data class FirestoreResponse<T>(
        val documents: List<Document<Map<String, FieldValue>>>
    )


    data class FirestoreDocumentResponse(
        val name: String
    )

    data class FirestoreDocumentRequest<T>(
        val fields: T
    )

    data class Document<T>(
        val name: String,
        val fields: T
    )

}
