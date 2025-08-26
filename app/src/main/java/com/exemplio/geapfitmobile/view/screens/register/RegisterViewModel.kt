package com.exemplio.geapfitmobile.view.screens.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplio.geapfitmobile.data.service.ApiServicesImpl
import com.exemplio.geapfitmobile.domain.entity.RegisterEntity
import com.exemplio.geapfitmobile.domain.entity.RoleType
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

    fun onUserChanged(user: String) {
        _uiState.update { state ->
            state.copy(
                user = user,
            )
        }
        verifyLogin()
    }

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
            val body = RegisterEntity(
                email = _uiState.value.email,
                password = _uiState.value.password,
                roleType = RoleType.STANDARD,
                userName = _uiState.value.user
            )
            val response = apiService.register(body)
            withContext(Dispatchers.Main) {
                if (response.success) {
                    _uiState.update { it.copy(isUserLogged = true, errorCode = null, errorMessage = null) }
                } else {
                    _uiState.update { it.copy(errorMessage = translate(response.errorMessage), errorCode = response.error as Int?) }
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
    val user: String = "",
    val isLoading: Boolean = false,
    val isLoginEnabled: Boolean = true,
    val isUserLogged:Boolean = false,
    var errorCode:Int? = null,
    var errorMessage:String? = null
)