package com.exemplio.geapfitmobile.view.screens.contacts

import com.exemplio.geapfitmobile.domain.entity.UserEntity


sealed class ContactsUiState {
    object Initial : ContactsUiState()
    object Loading : ContactsUiState()
    data class Loaded(val usuarios: List<UserEntity>) : ContactsUiState()
    data class Error(val message: String) : ContactsUiState()
}
