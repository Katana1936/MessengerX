package com.example.messengerx.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.messengerx.api.ApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "story_preferences")

class StoryDataStoreManager(private val context: Context) {

    companion object {
        private val STORIES_KEY = stringPreferencesKey("user_stories")
    }

    private val gson = Gson()

    val stories: Flow<List<ApiService.Story>> = context.dataStore.data.map { preferences ->
        preferences[STORIES_KEY]?.let { json ->
            val type = object : TypeToken<List<ApiService.Story>>() {}.type
            gson.fromJson<List<ApiService.Story>>(json, type)
        } ?: emptyList()
    }

    suspend fun saveStories(stories: List<ApiService.Story>) {
        val json = gson.toJson(stories)
        context.dataStore.edit { preferences ->
            preferences[STORIES_KEY] = json
        }
    }
}