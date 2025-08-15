package com.exemplio.geapfitmobile.view.screens.message


import MessageReceive
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplio.geapfitmobile.data.service.ConnectionState
import com.exemplio.geapfitmobile.data.service.WebSocketManager
import com.exemplio.geapfitmobile.data.service.ApiServicesImpl
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
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val apiService: ApiServicesImpl,
    private val ws: WebSocketManager = WebSocketManager()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientUiState())
//    val uiState: StateFlow<ClientUiState> = _uiState
    private val _message = MutableStateFlow<List<MessageReceive>>(emptyList())
    val message: StateFlow<List<MessageReceive>> = _message

    private val _latestMessage = MutableStateFlow<List<MessageReceive>>(emptyList())
    val latestMessage: StateFlow<List<MessageReceive>> = _latestMessage

    init {
        viewModelScope.launch {
            ws.incoming.collect { message ->
                println("Received message: $message")
                println("Received message3: ${ws.decodeMessage(message,ListSerializer(MessageReceive.serializer()))}")
                _latestMessage.value = ws.decodeMessage(message,ListSerializer(MessageReceive.serializer())).obj ?: emptyList()
            }
        }
    }

    fun getMessages() {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getMessages(
                mapOf<String, String?>(
                    "chatId" to "689818117bd6a653310456a0",
                )
            )
            Log.d("MessageViewModel", "Response: $response")
            val respuesta : List<MessageReceive>? = response.obj
            withContext(Dispatchers.Main) {
                GlobalScope.launch {
                    delay(500)
                    Log.d("MessageViewModel", "Respuesta: $respuesta")
                    if (respuesta != null) {
                        if (response.success) {
                            viewModelScope.launch {
                                val clientFieldsList = response?.obj?.let { it }
                                print("ClientFieldsList: $clientFieldsList")
                                if (clientFieldsList != null) {
                                    _message.value = clientFieldsList
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
        combine(ws.state, ws.lastError, _message) { state, err, msgs ->
            ChatUiState(
                disconnected = state == ConnectionState.Disconnected,
                connected = state == ConnectionState.Connected,
                connecting = state == ConnectionState.Connecting,
                messages = listOf("a", "b", "c"),
                lastError = err?.message
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ChatUiState())

    fun connect() {
        ws.connect()
    }

    fun send(text: String) {
        if (text.isNotBlank()) ws.send(text)
    }

    fun disconnect() {
        ws.close()
    }

    override fun onCleared() {
        super.onCleared()
        ws.close()
    }

//    fun sendSMS() {
//        loadingState(true)
//        viewModelScope.launch(Dispatchers.IO) {
//            val response = apiService.sendMessage(
//                Message(
//                    content = "Hello, how can I help you?",
//                    type = "text",
//                    username = "user123",
//                    receiver = "receiver123",
//                    sender = "sender123",
//                    chat = "chat123"
//                )
//            )
//            Log.d("MessageViewModel", "Response: $response")
//            val respuesta : VerifyPasswordResponse? = response.obj
//            withContext(Dispatchers.Main) {
//                GlobalScope.launch {
//                    delay(500)
//                    Log.d("MessageViewModel", "Respuesta: $respuesta")
//                    if (respuesta != null) {
//                        if (response.success) {
//                            viewModelScope.launch {
//                                val clientFieldsList = response?.obj
////                                _message.value = clientFieldsList
//                            }
//                            _uiState.update { it.copy(loaded = true, errorCode = null, errorMessage = null) }
//                        } else {
//                            Log.e("MessageViewModel", "Client failed: ${response.errorMessage}")
//                            _uiState.update { it.copy(errorMessage = translate(response.errorMessage), errorCode = 202) }
//                        }
//                    } else {
//                        _uiState.update { it.copy(errorMessage = translate(response.errorMessage), errorCode = 202) }
//                    }
//                    loadingState(false)
//                }
//            }
//        }
//    }

    private fun loadingState(isLoading: Boolean){
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun closeSession() {
        apiService.closeSession()
    }
}

data class ClientUiState(
    val isLoading: Boolean = false,
    val initialState:Boolean = true,
    var errorCode:Int? = null,
    var errorMessage:String? = null,
    var loaded:Boolean = false,
)

