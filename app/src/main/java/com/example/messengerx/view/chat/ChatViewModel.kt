import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messengerx.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val apiService: ApiService) : ViewModel() {

    private val _messages = MutableStateFlow<List<ApiService.MessageResponse>>(emptyList())
    val messages: StateFlow<List<ApiService.MessageResponse>> = _messages

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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
