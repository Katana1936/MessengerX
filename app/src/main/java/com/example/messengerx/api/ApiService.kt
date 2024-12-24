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

    @Headers("Accept: application/json")
    @GET("chats.json")
    suspend fun getChats(): Map<String, ChatResponse>?


    @Headers("Accept: application/json")
    @POST("chats.json")
    suspend fun createChat(@Body request: ChatRequest): Map<String, String>

    @Headers("Accept: application/json")
    @GET("chats/{chatId}/messages.json")
    suspend fun getMessages(@Path("chatId") chatId: String): Map<String, MessageResponse>

    @Headers("Accept: application/json")
    @POST("chats/{chatId}/messages.json")
    suspend fun sendMessage(@Path("chatId") chatId: String, @Body message: MessageRequest)

    @Headers("Accept: application/json")
    @GET("stories/{userId}/userStories.json")
    suspend fun getUserStories(@Path("userId") userId: String): Map<String, Story>

    @Headers("Accept: application/json")
    @POST("stories/{userId}/userStories.json")
    suspend fun addStory(@Path("userId") userId: String, @Body story: Story)

    @GET("stories.json")
    suspend fun getAllStories(): Map<String, Story>

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
        val isOnline: Boolean = false,
        val lastSeen: String = "",
        val participants: List<String> = emptyList(),
        val name: String = "",
        val timestamp: Long = 0L,
        val lastMessage: String = ""
    )

    data class MessageRequest(
        val senderId: String,
        val message: String,
        val timestamp: Long
    )

    data class MessageResponse(
        val senderId: String,
        val message: String,
        val timestamp: Long
    )
}
