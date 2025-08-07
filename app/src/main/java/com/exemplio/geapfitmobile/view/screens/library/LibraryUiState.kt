package com.exemplio.geapfitmobile.view.screens.library

import com.exemplio.geapfitmobile.domain.entity.UserEntity


data class LibraryUiState (
//    object Initial : LibraryUiState()
//    object Loading : LibraryUiState()
//    sealed class LibraryState
//    object LibraryInitialState : LibraryState()
//    object LibraryLoadingProductState : LibraryState()
//    sealed class LibraryEvent
//    object LibraryRefreshEvent : LibraryEvent()
//    object LibraryInitEvent : LibraryEvent()
//    data class LibraryLoadedProductState(val agenda: List<Any>) : LibraryState()
//    data class LibraryErrorProductState(val errorMessage: String = "Test screen") : LibraryState()
//    data class Loaded(val usuarios: List<UserEntity>) : LibraryUiState()
//    data class Error(val message: String) : LibraryUiState()
    val isLoading: Boolean = false,
    val initialState:Boolean = true,
    var errorCode:Int? = null,
    var errorMessage:String? = null,
    var loaded:Boolean = false,
)
