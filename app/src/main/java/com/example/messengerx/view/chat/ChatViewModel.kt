import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val apiService: ApiService) : ViewModel() {
    private val _chatList = MutableStateFlow<List<ApiService.ChatItem>>(emptyList())
    val chatList: StateFlow<List<ApiService.ChatItem>> = _chatList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Метод для загрузки списка чатов
    fun loadChats() {
        viewModelScope.launch {
            try {
                val response = apiService.getChats().execute()
                if (response.isSuccessful) {
                    val chatItems = response.body()?.map { (id, chatResponse) ->
                        ApiService.ChatItem(
                            id = id,
                            name = chatResponse.name ?: "Без имени",
                            photoUrl = chatResponse.photoUrl ?: ""
                        )
                    } ?: emptyList()
                    _chatList.value = chatItems
                } else {
                    _errorMessage.value = "Ошибка загрузки чатов: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка подключения: ${e.localizedMessage}"
            }
        }
    }
}
