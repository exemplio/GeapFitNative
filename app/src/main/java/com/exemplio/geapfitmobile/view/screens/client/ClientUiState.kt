package com.exemplio.geapfitmobile.view.screens.client

data class ClientUiState(
    val isLoading: Boolean = false,
    val initialState:Boolean = true,
    var errorCode:Int? = null,
    var errorMessage:String? = null,
    var loaded:Boolean = false,
)
