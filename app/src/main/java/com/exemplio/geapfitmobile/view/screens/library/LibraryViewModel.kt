package com.exemplio.geapfitmobile.view.home.screens.library

import ClientFields
import ClientsResponse
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplio.geapfitmobile.data.service.ApiServicesImpl
import com.exemplio.geapfitmobile.view.screens.library.LibraryUiState
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
class LibraryViewModel @Inject constructor(private val apiService: ApiServicesImpl) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState
    private val _library = MutableStateFlow<List<ClientFields>>(emptyList())
    val library: StateFlow<List<ClientFields>> = _library

    fun getClients() {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getClients()
            Log.d("LibraryViewModel", "Response: $response")
            val respuesta : ClientsResponse? = response.obj
            withContext(Dispatchers.Main) {
                Log.d("LibraryViewModel", "Respuesta: $respuesta")
                if (respuesta != null) {
                    if (response.success) {
                        viewModelScope.launch {
                            val clientFieldsList = response?.obj?.documents?.map { it.fields } ?: emptyList()
                            _library.value = clientFieldsList
                        }
                        _uiState.update { it.copy(loaded = true, errorCode = null, errorMessage = null) }
                    } else {
                        Log.e("LibraryViewModel", "Client failed: ${response.errorMessage}")
                        _uiState.update { it.copy(errorMessage = translate(response.errorMessage), errorCode = 202) }
                    }
                } else {
                    _uiState.update { it.copy(errorMessage = translate(response.errorMessage), errorCode = 202) }
                }
                loadingState(false)
            }
        }
    }

    fun closeSession() {
        apiService.closeSession()
    }

    private fun loadingState(isLoading: Boolean){
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}

