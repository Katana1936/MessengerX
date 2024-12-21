package com.example.messengerx.view.stories

import androidx.lifecycle.ViewModel
import com.example.messengerx.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(private val apiService: ApiService) : ViewModel() {

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    fun fetchStories(userId: String) {
        apiService.getUserStories(userId).enqueue(object : Callback<Map<String, Story>> {
            override fun onResponse(call: Call<Map<String, Story>>, response: Response<Map<String, Story>>) {
                if (response.isSuccessful) {
                    val storyList = response.body()?.values?.toList() ?: emptyList()
                    _stories.value = storyList
                } else {
                    println("Ошибка получения историй: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Story>>, t: Throwable) {
                println("Ошибка сети: ${t.message}")
            }
        })
    }

    fun addStory(userId: String, story: Story, onComplete: () -> Unit) {
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


