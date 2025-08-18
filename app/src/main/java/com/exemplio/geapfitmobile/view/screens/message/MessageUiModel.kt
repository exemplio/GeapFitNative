package com.exemplio.geapfitmobile.view.screens.message

data class ChatUiState(
    val disconnected: Boolean = true,
    val connected: Boolean = false,
    val connecting: Boolean = false,
    val messages: List<String> = emptyList(),
    val lastError: String? = null,
    var errorCode: Int? = null,
    var errorMessage: String? = null,
)