package com.example.messengerx.view.stories

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.messengerx.api.ApiService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(private val apiService: ApiService) : ViewModel() {

    private val _stories = MutableStateFlow<List<ApiService.Story>>(emptyList())
    val stories: StateFlow<List<ApiService.Story>> = _stories

    fun fetchStories(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getUserStories(userId).execute()
                if (response.isSuccessful) {
                    val storyList = response.body()?.values?.toList() ?: emptyList()
                    _stories.value = storyList
                } else {
                    println("Ошибка получения историй: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Ошибка сети: ${e.message}")
            }
        }
    }


    fun uploadStoryImage(
        userId: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val storyRef = storageRef.child("stories/$userId/${System.currentTimeMillis()}.jpg")

        storyRef.putFile(imageUri)
            .addOnSuccessListener {
                storyRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener { e ->
                    onFailure("Ошибка получения URL: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                onFailure("Ошибка загрузки файла: ${e.message}")
            }
    }

    fun addStory(userId: String, story: ApiService.Story, onComplete: () -> Unit) {
        apiService.addStory(userId, story).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onComplete()
                } else {
                    println("Ошибка добавления истории: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка сети: ${t.message}")
            }
        })
    }
}
