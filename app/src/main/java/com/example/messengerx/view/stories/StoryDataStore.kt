import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.messengerx.api.ApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.storyDataStore by preferencesDataStore(name = "stories")

class StoryDataStore(private val context: Context) {
    private val gson = Gson()
    private val storyKey = stringPreferencesKey("stories")

    // Загрузка историй
    val stories: Flow<List<ApiService.Story>> = context.storyDataStore.data.map { preferences ->
        val json = preferences[storyKey] ?: "[]"
        gson.fromJson(json, object : TypeToken<List<ApiService.Story>>() {}.type)
    }

    // Сохранение историй
    suspend fun saveStories(storyList: List<ApiService.Story>) {
        val json = gson.toJson(storyList)
        context.storyDataStore.edit { preferences ->
            preferences[storyKey] = json
        }
    }
}
