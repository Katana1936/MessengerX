package com.example.messengerx.api

import com.example.messengerx.models.ChatRequest
import com.example.messengerx.models.ChatResponse
import com.example.messengerx.models.ContactRequest
import com.example.messengerx.models.ContactResponse
import retrofit2.http.*

interface ApiService {
    @GET("{node}.json")
    suspend fun getData(@Path("node") node: String): Map<String, Any>

    @POST("{node}.json")
    suspend fun postData(@Path("node") node: String, @Body data: Any): Map<String, String>

    @PUT("{node}/{id}.json")
    suspend fun putData(@Path("node") node: String, @Path("id") id: String, @Body data: Any): Map<String, String>

    @DELETE("{node}/{id}.json")
    suspend fun deleteData(@Path("node") node: String, @Path("id") id: String): Map<String, String>
}
