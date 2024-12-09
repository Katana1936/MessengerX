package com.example.messengerx.api

import android.content.Context
import android.content.Intent
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://messengerx-df3ea-default-rtdb.europe-west1.firebasedatabase.app/"

    private lateinit var retrofit: Retrofit

    fun getInstance(context: Context): ApiService {
        if (!::retrofit.isInitialized) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val authInterceptor = Interceptor { chain ->
                val token = SharedPrefsHelper.getToken(context)
                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                requestBuilder.addHeader("Accept", "application/json")
                val response = chain.proceed(requestBuilder.build())

                // Обработка 401 Unauthorized
                if (response.code == 401) {
                    handleUnauthorized(context)
                }

                response
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .client(client)
                .build()
        }
        return retrofit.create(ApiService::class.java)
    }

    private fun handleUnauthorized(context: Context) {
        SharedPrefsHelper.clearToken(context)
        CoroutineScope(Dispatchers.IO).launch {
            ProfileDataStoreManager(context).clearProfileData()
        }
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}
