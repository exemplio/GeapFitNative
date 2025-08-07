package com.exemplio.geapfitmobile.view.home.screens.client

import com.exemplio.geapfitmobile.domain.entity.UserEntity


sealed class ClientUiState {
    object Initial : ClientUiState()
    object Loading : ClientUiState()
    data class Loaded(val usuarios: List<UserEntity>) : ClientUiState()
    data class Error(val message: String) : ClientUiState()
}
