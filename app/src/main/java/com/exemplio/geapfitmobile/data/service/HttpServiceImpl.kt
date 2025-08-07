package com.exemplio.geapfitmobile.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Inject


class HttpServiceImpl @Inject constructor(
    private val client: OkHttpClient
) {
    suspend fun response(
        f: suspend (OkHttpClient) -> Response
    ): Response = withContext(Dispatchers.IO) {
        f(client)
    }

    suspend fun responseBody(
        f: suspend (OkHttpClient) -> Response
    ): String? = withContext(Dispatchers.IO) {
        val response = f(client)
        response.body?.string()
    }

}