package com.exemplio.geapfitmobile.view.screens.chats

import ClientFields
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
    private val _chats = MutableStateFlow<List<ClientFields>>(emptyList())
    val chats: StateFlow<List<ClientFields>> = _chats

    fun getClients() {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getClients()
            Log.d("ChatsViewModel", "Response: $response")
            val respuesta : ClientsResponse? = response.obj
            withContext(Dispatchers.Main) {
                Log.d("ChatsViewModel", "Respuesta: $respuesta")
                if (respuesta != null) {
                    if (response.success) {
                        viewModelScope.launch {
                            val clientFieldsList = response?.obj?.documents?.map { it.fields } ?: emptyList()
                            _chats.value = clientFieldsList
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

