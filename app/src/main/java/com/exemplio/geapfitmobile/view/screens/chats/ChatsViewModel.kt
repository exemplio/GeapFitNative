package com.exemplio.geapfitmobile.view.screens.chats

import ChatItem
import Client
import ClientsResponse
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplio.geapfitmobile.data.service.ApiServicesImpl
import com.exemplio.geapfitmobile.view.home.screens.client.ChatsUiState
import com.geapfit.utils.translate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(private val apiService: ApiServicesImpl) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatsUiState())
    val uiState: StateFlow<ChatsUiState> = _uiState
    private val _chats = MutableStateFlow<List<ChatItem>>(emptyList())
    val chats: StateFlow<List<ChatItem>> = _chats

    fun getClients() {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getChats()
            Log.d("ChatsViewModel", "Response: $response")
            val respuesta : List<ChatItem>? = response.obj
            withContext(Dispatchers.Main) {
                Log.d("ChatsViewModel", "Respuesta: $respuesta")
                if (respuesta != null) {
                    if (response.success) {
                        viewModelScope.launch {
                            val chatFieldList = response?.obj?.map { chat ->
                                ChatItem(
                                    name = chat.name,
                                    lastMessage = chat.lastMessage,
                                    chatId = chat.chatId,
                                    date = chat.date,
                                    members = chat.members
                                )
                            }
                            if (chatFieldList != null) {
                                _chats.value = chatFieldList
                            }
                        }
                        _uiState.update { it.copy(loaded = true, errorCode = null, errorMessage = null) }
                    } else {
                        Log.e("ChatsViewModel", "Client failed: ${response.errorMessage}")
                        _uiState.update { it.copy(errorMessage = translate(response.errorMessage), errorCode = 202) }
                    }
                } else {
                    _uiState.update { it.copy(errorMessage = translate(response.errorMessage), errorCode = 202) }
                }
                loadingState(false)
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

