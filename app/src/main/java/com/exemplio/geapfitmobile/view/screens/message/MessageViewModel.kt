package com.exemplio.geapfitmobile.view.screens.message


import Client
import ClientsResponse
import Message
import MessageModel
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplio.geapfitmobile.data.service.ApiServicesImpl
import com.exemplio.geapfitmobile.domain.entity.VerifyPasswordResponse
import com.exemplio.geapfitmobile.view.screens.client.ClientUiState
import com.geapfit.utils.translate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val apiService: ApiServicesImpl) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientUiState())
    val uiState: StateFlow<ClientUiState> = _uiState
    private val _message = MutableStateFlow<List<MessageModel>>(emptyList())
    val message: StateFlow<List<MessageModel>> = _message

    fun getMessages() {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getMessages(
                mapOf<String, String?>(
                    "chatId" to "689818117bd6a653310456a0",
                )
            )
            Log.d("MessageViewModel", "Response: $response")
            val respuesta : List<MessageModel>? = response.obj
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

    fun sendSMS() {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.sendMessage(
                Message(
                    content = "Hello, how can I help you?",
                    type = "text",
                    username = "user123",
                    receiver = "receiver123",
                    sender = "sender123",
                    chat = "chat123"
                )
            )
            Log.d("MessageViewModel", "Response: $response")
            val respuesta : VerifyPasswordResponse? = response.obj
            withContext(Dispatchers.Main) {
                GlobalScope.launch {
                    delay(500)
                    Log.d("MessageViewModel", "Respuesta: $respuesta")
                    if (respuesta != null) {
                        if (response.success) {
                            viewModelScope.launch {
                                val clientFieldsList = response?.obj
//                                _message.value = clientFieldsList
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

