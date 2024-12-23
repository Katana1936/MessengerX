import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val apiService: ApiService) : ViewModel() {

    // Для сообщений
    private val _messages = MutableStateFlow<List<ApiService.MessageResponse>>(emptyList())
    val messages: StateFlow<List<ApiService.MessageResponse>> = _messages

    private val _chatList = MutableStateFlow<List<ApiService.ChatItem>>(emptyList())
    val chatList: StateFlow<List<ApiService.ChatItem>> = _chatList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Загрузка списка чатов
    fun loadChats() {
        viewModelScope.launch {
            try {
                val response = apiService.getChats().execute()
                if (response.isSuccessful) {
                    val chatItems = response.body()?.map { (id, chatResponse) ->
                        ApiService.ChatItem(
                            id = id,
                            name = chatResponse.name ?: "Без имени"
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

    // Загрузка сообщений
    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getMessages(chatId).execute()
                if (response.isSuccessful) {
                    _messages.value = response.body()?.values?.sortedBy { it.timestamp } ?: emptyList()
                } else {
                    _errorMessage.value = "Ошибка загрузки сообщений: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.localizedMessage}"
            }
        }
    }

    // Отправка сообщения
    fun sendMessage(chatId: String, senderId: String, message: String) {
        viewModelScope.launch {
            try {
                val messageRequest = ApiService.MessageRequest(senderId, message, System.currentTimeMillis())
                val response = apiService.sendMessage(chatId, messageRequest).execute()
                if (response.isSuccessful) {
                    loadMessages(chatId)
                } else {
                    _errorMessage.value = "Ошибка отправки сообщения: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка отправки сообщения: ${e.localizedMessage}"
            }
        }
    }
}
