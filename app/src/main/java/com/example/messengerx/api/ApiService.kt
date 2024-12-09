package com.example.messengerx.api

import com.example.messengerx.view.chat.ChatRequest
import com.example.messengerx.view.chat.ChatResponse
import com.example.messengerx.view.chat.MessageRequest
import com.example.messengerx.view.chat.MessageResponse
import com.example.messengerx.view.contact.ContactRequest
import com.example.messengerx.view.contact.ContactResponse
import retrofit2.Call
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
    fun getContacts(): Call<Map<String, ContactResponse>>

    @Headers("Accept: application/json")
    @POST("contacts.json")
    fun addContact(@Body request: ContactRequest): Call<Map<String, String>>

    @Headers("Accept: application/json")
    @PUT("contacts/{id}.json")
    fun updateContact(
        @Path("id") id: String,
        @Body request: ContactRequest
    ): Call<Map<String, String>>

    @Headers("Accept: application/json")
    @DELETE("contacts/{id}.json")
    fun deleteContact(@Path("id") id: String): Call<Map<String, String>>

    @Headers("Accept: application/json")
    @GET("chats.json")
    fun getChats(): Call<Map<String, ChatResponse>>

    @Headers("Accept: application/json")
    @POST("chats.json")
    fun createChat(@Body request: ChatRequest): Call<Map<String, String>>

    @Headers("Accept: application/json")
    @GET("chats/{chatId}/messages.json")
    fun getMessages(@Path("chatId") chatId: String): Call<Map<String, MessageResponse>>

    @Headers("Accept: application/json")
    @POST("chats/{chatId}/messages.json")
    fun sendMessage(@Path("chatId") chatId: String, @Body message: MessageRequest): Call<Void>


}

