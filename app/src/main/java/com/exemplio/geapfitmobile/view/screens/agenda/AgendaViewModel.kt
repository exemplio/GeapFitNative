package com.exemplio.geapfitmobile.view.screens.agenda

import Client
import ClientsResponse
import android.util.Log
import android.util.Patterns
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
class AgendaViewModel @Inject constructor(private val apiService: ApiServicesImpl) : ViewModel() {

    private val _uiState = MutableStateFlow(AgendaUiState())
    val uiState: StateFlow<AgendaUiState> = _uiState
    private val _agendas = MutableStateFlow<List<Client>>(emptyList())
    val agendas: StateFlow<List<Client>> = _agendas

    fun getAgenda() {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getClients()
            Log.d("AgendaViewModel", "Response: $response")
            val respuesta : ClientsResponse? = response.obj
            withContext(Dispatchers.Main) {
                GlobalScope.launch {
                    delay(500)
                    Log.d("AgendaViewModel", "Respuesta: $respuesta")
                    if (respuesta != null) {
                        if (response.success) {
                            viewModelScope.launch {
                                val agendaFieldsList =
                                    response?.obj?.data?.map { it } ?: emptyList()
                                _agendas.value = agendaFieldsList
                            }
                            _uiState.update {
                                it.copy(
                                    loaded = true,
                                    errorCode = null,
                                    errorMessage = null
                                )
                            }
                        } else {
                            Log.e("AgendaViewModel", "Client failed: ${response.errorMessage}")
                            _uiState.update {
                                it.copy(
                                    errorMessage = translate(response.errorMessage),
                                    errorCode = 202
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                errorMessage = translate(response.errorMessage),
                                errorCode = 202
                            )
                        }
                    }
                    loadingState(false)
                }
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
