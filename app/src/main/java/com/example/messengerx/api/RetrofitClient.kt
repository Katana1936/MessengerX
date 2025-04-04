package com.example.messengerx.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL =
        "https://firestore.googleapis.com/v1/projects/messengerx-df3ea/databases/(default)/documents/"

    private lateinit var retrofit: Retrofit
    private var currentToken: String? = null

    fun getInstance(token: String? = null): ApiService {
        if (token != null) {
            currentToken = token
        }

        if (!::retrofit.isInitialized || currentToken != token) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val authInterceptor = Interceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                if (!currentToken.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $currentToken")
                }
                chain.proceed(requestBuilder.build())
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        return retrofit.create(ApiService::class.java)
    }
}

