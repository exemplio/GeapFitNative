package com.exemplio.geapfitmobile.view.screens.agenda

import com.exemplio.geapfitmobile.domain.entity.UserEntity


data class AgendaUiState(
//    object Initial : AgendaUiState()
//    object Loading : AgendaUiState()
//    sealed class AgendaState
//    object AgendaInitialState : AgendaState()
//    object AgendaLoadingProductState : AgendaState()
//    sealed class AgendaEvent
//    object AgendaRefreshEvent : AgendaEvent()
//    object AgendaInitEvent : AgendaEvent()
//    data class AgendaLoadedProductState(val agenda: List<Any>) : AgendaState()
//    data class AgendaErrorProductState(val errorMessage: String = "Test screen") : AgendaState()
//    data class Loaded(val usuarios: List<UserEntity>) : AgendaUiState()
//    data class Error(val message: String) : AgendaUiState()
    val isLoading: Boolean = false,
    val initialState:Boolean = true,
    var errorCode:Int? = null,
    var errorMessage:String? = null,
    var loaded:Boolean = false,
)
