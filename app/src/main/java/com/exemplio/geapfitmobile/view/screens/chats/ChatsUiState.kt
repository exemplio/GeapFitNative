package com.exemplio.geapfitmobile.view.home.screens.client

import com.exemplio.geapfitmobile.domain.entity.UserEntity


data class ChatsUiState (
//    object Initial : ChatsUiState()
//    object Loading : ChatsUiState()
//    data class Loaded(val usuarios: List<UserEntity>) : ChatsUiState()
//    data class Error(val message: String) : ChatsUiState()
    val isLoading: Boolean = false,
    val initialState:Boolean = true,
    var errorCode:Int? = null,
    var errorMessage:String? = null,
    var loaded:Boolean = false,
)
