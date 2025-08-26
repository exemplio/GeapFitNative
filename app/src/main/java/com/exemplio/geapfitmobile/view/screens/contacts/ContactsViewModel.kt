package com.exemplio.geapfitmobile.view.screens.contacts


import Client
import ClientsResponse
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplio.geapfitmobile.data.service.ApiServicesImpl
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
class ContactsViewModel @Inject constructor(private val apiService: ApiServicesImpl) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState
    private val _contacts = MutableStateFlow<List<Client>>(emptyList())
    val contacts: StateFlow<List<Client>> = _contacts

    fun getClients() {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getClients()
            Log.d("ClientViewModel", "Response: $response")
            val respuesta : ClientsResponse? = response.obj
            withContext(Dispatchers.Main) {
                GlobalScope.launch {
                    delay(500)
                    Log.d("ClientViewModel", "Respuesta: $respuesta")
                    if (respuesta != null) {
                        if (response.success) {
                            viewModelScope.launch {
                                val clientFieldsList = response?.obj?.data?.map { it } ?: emptyList()
                                _contacts.value = clientFieldsList
                            }
                            _uiState.update { it.copy(loaded = true, errorCode = null, errorMessage = null) }
                        } else {
                            Log.e("ClientViewModel", "Client failed: ${response.errorMessage}")
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

