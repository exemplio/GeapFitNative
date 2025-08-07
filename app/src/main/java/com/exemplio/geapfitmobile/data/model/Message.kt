package com.exemplio.geapfitmobile.data.model

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("message") val message: String?,
    @SerializedName("cause") val cause: List<String>? = null
)