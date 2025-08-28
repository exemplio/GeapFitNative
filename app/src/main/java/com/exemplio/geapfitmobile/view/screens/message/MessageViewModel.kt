package com.exemplio.geapfitmobile.view.screens.message


import ReceiveMessageModel
import SendMessageModel
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplio.geapfitmobile.data.service.ConnectionState
import com.exemplio.geapfitmobile.data.service.WebSocketManager
import com.exemplio.geapfitmobile.data.service.ApiServicesImpl
import com.exemplio.geapfitmobile.utils.CacheService
import com.exemplio.geapfitmobile.view.screens.client.ClientUiState
import com.geapfit.utils.translate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val apiService: ApiServicesImpl,
    private val ws: WebSocketManager,
    private val cache: CacheService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientUiState())
    private val _messages = MutableStateFlow<List<ReceiveMessageModel?>>(emptyList())
    val messages: StateFlow<List<ReceiveMessageModel?>> = _messages
    val userId = cache.credentialResponse()?._id
    private var thirdUserId: String? = null
    private var receiveChatId: String? = null
    private val _webSocketStatus = MutableStateFlow(ConnectionState.Disconnected)
    val webSocketStatus: StateFlow<ConnectionState> = _webSocketStatus
    private var reconnectAttempts = 0
    private val maxReconnectDelay = 60_000L

    fun setUserId(userId: String?) {
        this.thirdUserId = userId
    }

    fun setReceiveChatId(chatId: String?) {
        this.receiveChatId = chatId
    }

    fun addMessage(message: ReceiveMessageModel?) {
        _messages.value = _messages.value + message
    }

    init {
        connectWebSocket()
    }

    private fun connectWebSocket() {
        viewModelScope.launch {
            _webSocketStatus.value = if (reconnectAttempts == 0) ConnectionState.Disconnected else ConnectionState.Connecting
            try {
                ws.connect()
                _webSocketStatus.value = ConnectionState.Connected
                ws.incoming.collect { message ->
                    try {
                        val newMessages = ws.decodeMessage(message, MessageWss.serializer()).obj?.message
                        if (newMessages != null) {
                            _messages.value = _messages.value + newMessages
                        }
                    } catch (e: Exception) {
                        Log.e("MessageViewModel", "Error decoding message: $message", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("MessageViewModel", "WebSocket failure: ${e.message}", e)
                _webSocketStatus.value = ConnectionState.Failed
                reconnectWithBackoff()
            }
        }
    }

    private fun reconnectWithBackoff() {
        viewModelScope.launch {
            reconnectAttempts++
            val delayMillis = (2.0.pow(reconnectAttempts.toDouble()) * 1000L).toLong().coerceAtMost(maxReconnectDelay)
            delay(delayMillis)
            connectWebSocket()
        }
    }

    fun manualReconnect() {
        reconnectAttempts = 0
        connectWebSocket()
    }

    fun getMessages(receiveData : String?) {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = if (receiveChatId=="empty") {
                apiService.getMessages(
                    mapOf<String, String?>(
                        "userId" to receiveData,
                    )
                )
            } else{
                apiService.getMessages(
                    mapOf<String, String?>(
                        "chatId" to receiveData,
                    )
                )
            }
            Log.d("MessageViewModel", "Response: $response")
            withContext(Dispatchers.Main) {
                GlobalScope.launch {
                    delay(250)
                    Log.d("MessageViewModel", "Respuesta: $response")
                    if (response.success) {
                        println("Deberia updatear loaded true2")
                        if (response.obj?.isEmpty() ?: true) {
                            viewModelScope.launch {
                                val messagesList = response?.obj?.let { it }
                                if (messagesList != null) {
                                    _messages.value = messagesList
                                }
                            }
                        }
                        loadingState(false)
                        _uiState.update { it.copy(loaded = true, errorCode = null, errorMessage = null, isLoading = false) }
                    } else {
                        Log.e("MessageViewModel", "Client failed: ${response.errorMessage}")
                        _uiState.update { it.copy(errorMessage = translate(response.errorMessage), errorCode = 202) }
                    }
                    loadingState(false)
                }
            }
        }
    }

    val uiState: StateFlow<ChatUiState> =
        combine(ws.state, ws.lastError, _messages) { state, err, msgs ->
            ChatUiState(
                disconnected = state == ConnectionState.Disconnected,
                connected = state == ConnectionState.Connected,
                connecting = state == ConnectionState.Connecting,
                messages = listOf("a", "b", "c"),
                lastError = err?.message,
                isLoading = _uiState.value.isLoading,
                loaded = _uiState.value.loaded,
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ChatUiState())

    fun connect() {
        ws.connect()
    }

    fun send(content: String?) {
        val msg = SendMessageModel(type = "send", content = content, userName = "exemplio", receiverId = thirdUserId, senderId = userId, chatId = receiveChatId)
        val json = Json.encodeToString(SendMessageModel.serializer(), msg)
        if (json.isNotBlank()) ws.send(json)
    }

    fun disconnect() {
        ws.close()
    }

    override fun onCleared() {
        super.onCleared()
        ws.close()
    }

    private fun loadingState(isLoading: Boolean){
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun closeSession() {
        apiService.closeSession()
    }
}

