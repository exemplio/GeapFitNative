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

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val apiService: ApiServicesImpl,
    private val ws: WebSocketManager,
    private val cache: CacheService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientUiState())
    private val _messages = MutableStateFlow<List<ReceiveMessageModel?>>(emptyList())
    val messages: StateFlow<List<ReceiveMessageModel?>> = _messages
    private var thirdUserId: String? = null
    private var receiveChatId: String? = null

    fun setUserId(userId: String?) {
        this.thirdUserId = userId
    }

    fun setReceiveChatId(chatId: String?) {
        this.receiveChatId = chatId
    }

    init {
        viewModelScope.launch {
            ws.incoming.collect { message ->
                try {
                    println("You receive: $message")
                    val newMessages= ws.decodeMessage(message, MessageWss.serializer()).obj?.message
                    println("Decoded messages: ${ws.decodeMessage(message, MessageWss.serializer())}")
                    _messages.value = _messages.value + newMessages
                }catch (e: Exception) {
                    Log.e("MessageViewModel", "Error decoding message: $message", e)
                }
            }
        }
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
            val respuesta : List<ReceiveMessageModel>? = response.obj
            withContext(Dispatchers.Main) {
                GlobalScope.launch {
                    delay(500)
                    Log.d("MessageViewModel", "Respuesta: $respuesta")
                    if (respuesta != null) {
                        if (response.success) {
                            viewModelScope.launch {
                                val clientFieldsList = response?.obj?.let { it }
                                if (clientFieldsList != null) {
                                    _messages.value = clientFieldsList
                                }
                            }
                            _uiState.update { it.copy(loaded = true, errorCode = null, errorMessage = null) }
                        } else {
                            Log.e("MessageViewModel", "Client failed: ${response.errorMessage}")
                            _uiState.update { it.copy(errorMessage = translate(response.errorMessage), errorCode = 202) }
                        }
                    } else {
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
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ChatUiState())

    fun connect() {
        ws.connect()
    }

    fun send(content: String?) {
//        val msg = SendMessageModel(type = "send", content = content, userName = "exemplio", receiverId = thirdUserId, senderId = cache.credentialResponse()?.userId, chatId = receiveChatId)
        val msg = SendMessageModel(type = "send", content = content, userName = "exemplio", receiverId = "d38a55bf-f691-461d-bac4-e7ffddac376b", senderId = cache.credentialResponse()?.userId, chatId = receiveChatId)
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

