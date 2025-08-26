package com.exemplio.geapfitmobile.view.screens.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplio.geapfitmobile.data.service.ApiServicesImpl
import com.exemplio.geapfitmobile.domain.entity.PasswordGrantEntity
import com.exemplio.geapfitmobile.domain.entity.VerifyPasswordResponse
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
class RegisterViewModel @Inject constructor(val apiService: ApiServicesImpl): ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState:StateFlow<RegisterUiState> = _uiState

    fun onEmailChanged(email: String) {
        _uiState.update { state ->
            state.copy(
                email = email,
            )
        }
        verifyLogin()
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(password = password)
        }
        verifyLogin()
    }

    private fun verifyLogin() {
        val enabledLogin =
            isEmailValid(_uiState.value.email) && isPasswordValid(_uiState.value.password)
        _uiState.update {
            it.copy(isLoginEnabled = enabledLogin)
        }
    }

    private fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isPasswordValid(password: String): Boolean = password.length >= 6

    fun onClickSelected() {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val body = PasswordGrantEntity(
                email = _uiState.value.email,
                password = _uiState.value.password,
            )
            val response = apiService.register(body)
            Log.d("LoginViewModel", "Response: $response")
            val respuesta : VerifyPasswordResponse? = response?.obj
            withContext(Dispatchers.Main) {
                if (response != null) {
                    if (response.success) {
                        _uiState.update { it.copy(isUserLogged = true, errorCode = null, errorMessage = null) }
                    } else {
                        Log.e("LoginViewModel", "Login failed: ${response.errorMessage}")
                        _uiState.update { it.copy(errorMessage = translate(response.errorMessage), errorCode = 202) }
                    }
                }
                loadingState(false)
            }
        }
    }

    private fun loadingState(isLoading: Boolean){
        _uiState.update { it.copy(isLoading = isLoading) }
    }



}

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginEnabled: Boolean = true,
    val isUserLogged:Boolean = false,
    var errorCode:Int? = null,
    var errorMessage:String? = null
)