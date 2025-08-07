package com.exemplio.geapfitmobile.view.auth.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplio.geapfitmobile.utils.CacheService
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
class LoginViewModel @Inject constructor(val login: Login, private val cache: CacheService) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState
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

    fun onClickSelected() {
        loadingState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = login.login(_uiState.value.email, _uiState.value.password)
            Log.d("LoginViewModel", "Response: $response")
            val respuesta : VerifyPasswordResponse? = response?.obj
            withContext(Dispatchers.Main) {
                if (response != null) {
                    if (response.success) {
                        cache.saveLastCredentials(
                            VerifyPasswordResponse(
                                respuesta?.kind ?: "",
                                respuesta?.localId ?: "",
                                respuesta?.email ?: "",
                                idToken = respuesta?.idToken ?: "",
                                displayName = respuesta?.displayName ?: "",
                                registered = respuesta?.registered ?: false,
                            )
                        )
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

    fun check(){
       if(cache.areCredentialsStored()){
              _uiState.update { it.copy(isUserLogged = true) }
         } else {
              _uiState.update { it.copy(isUserLogged = false) }
       }
    }

    private fun loadingState(isLoading: Boolean){
        _uiState.update { it.copy(isLoading = isLoading) }
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
}

data class LoginUiState(
    val email: String = "ricardo@prueba.com",
    val password: String = "12345678",
    val isLoading: Boolean = false,
    val isLoginEnabled: Boolean = true,
    val isUserLogged:Boolean = false,
    var errorCode:Int? = null,
    var errorMessage:String? = null
)

