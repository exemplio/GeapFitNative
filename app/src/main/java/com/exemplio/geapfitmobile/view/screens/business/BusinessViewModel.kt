package com.exemplio.geapfitmobile.view.auth.business

import ClientDocument
import ClientFields
import ClientsResponse
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplio.geapfitmobile.data.service.ApiServicesImpl
import com.exemplio.geapfitmobile.view.auth.login.LoginUiState
import com.exemplio.geapfitmobile.view.home.screens.agenda.BusinessUiState
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
class BusinessViewModel @Inject constructor(private val apiService: ApiServicesImpl) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessUiState())
    val uiState: StateFlow<BusinessUiState> = _uiState
    private val _businessInfo = MutableStateFlow<List<ClientFields>>(emptyList())
    val businessInfo: StateFlow<List<ClientFields>> = _businessInfo

    fun getClients() {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.getClients()
            Log.d("BusinessViewModel", "Response: $response")
            val respuesta : ClientsResponse? = response.obj
            withContext(Dispatchers.Main) {
                Log.d("BusinessViewModel", "Respuesta: $respuesta")
                if (respuesta != null) {
                    if (response.success) {
                        viewModelScope.launch {
                            val clientFieldsList = response?.obj?.documents?.map { it.fields } ?: emptyList()
                            _businessInfo.value = clientFieldsList
                        }
                        _uiState.update { it.copy(loaded = true, errorCode = null, errorMessage = null) }
                    } else {
                        Log.e("BusinessViewModel", "Client failed: ${response.errorMessage}")
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

