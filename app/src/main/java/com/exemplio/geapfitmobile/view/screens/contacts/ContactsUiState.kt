package com.exemplio.geapfitmobile.view.screens.contacts

import com.exemplio.geapfitmobile.domain.entity.UserEntity


data class ContactsUiState (
    val isLoading: Boolean = false,
    val initialState:Boolean = true,
    var errorCode:Int? = null,
    var errorMessage:String? = null,
    var loaded:Boolean = false,
)