package com.exemplio.geapfitmobile.data.service

import ChatItem
import ClientsResponse
import ErrorResponse
import HttpUtil
import ReceiveMessageModel
import android.util.Log
import com.exemplio.geapfitmobile.utils.CacheService
import com.exemplio.geapfitmobile.domain.entity.PasswordGrantEntity
import com.exemplio.geapfitmobile.data.model.Resultado
import com.exemplio.geapfitmobile.domain.entity.RegisterEntity
import com.exemplio.geapfitmobile.domain.entity.UserEntity
import com.exemplio.geapfitmobile.utils.MyUtils
import com.exemplio.geapfitmobile.utils.StaticNamesPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class ApiServicesImpl(
    private val httpService: HttpServiceImpl,
    private val isOnlineProvider: IsOnlineProvider,
    private val cache: CacheService
) {

    private fun url(unencodedPath: String, queryParameters: Map<String, Any>? = null): Result<HttpUrl> {
        return try {
                val builder = HttpUrl.Builder()
                    .scheme("https")
                    .host(MyUtils.base)
                    .addEncodedPathSegments(unencodedPath)
                queryParameters?.forEach { (key, value) ->
                    builder.addQueryParameter(key, value.toString())
                }
                println("Http URL: ${builder.build()}")
                Result.success(builder.build())
        } catch (e: Exception) {
            Log.e("UrlError", "Unexpected error in url builder", e)
            return Result.failure(e)
        }
    }

    private fun urlAuth(unencodedPath: String, queryParameters: Map<String, Any>? = null): Result<HttpUrl> {
        return try {
            val builder = HttpUrl.Builder()
                .scheme("https")
                .host(MyUtils.baseAuth)
                .addEncodedPathSegments(unencodedPath)
            queryParameters?.forEach { (key, value) ->
                builder.addQueryParameter(key, value.toString())
            }
            println("Http URL: ${builder.build()}")
            Result.success(builder.build())
        } catch (e: Exception) {
            Log.e("UrlError", "Unexpected error in url builder", e)
            return Result.failure(e)
        }
    }

    private suspend inline fun <reified T> httpCall(
        noinline f: (OkHttpClient) -> Response,
        serializer: KSerializer<T>,
    ): Resultado<T?> = withContext(Dispatchers.IO) {
        try {
            val isOnline = isOnlineProvider.isOnline()
            if (isOnline) {
                val response = httpService.response(f)
                var result: T? = null
                var apiError: ErrorResponse? = null
                if (response.code!=200){
                    apiError = response.body?.string()?.let { Json.decodeFromString<ErrorResponse>(it) }
                }else{
                    result = Json.decodeFromString(serializer, response.body?.string() ?: "")
                }
                HttpUtil.result(response, apiError, result).let { resultx ->
                    if (response.isSuccessful) {
                        Resultado.success(resultx.obj)
                    } else {
                        Resultado.identity(resultx)
                    }
                }
            }
            else {
                Resultado.failMsg<T?>("No posee conexión a internet",0)
            }
        } catch (e: Exception) {
            println("Caught exception: ${e.message}")
            Resultado.failMsg("Ocurrió un error inesperado", error = 0)
        }
    }

    suspend fun passwordGrant(request: PasswordGrantEntity): Resultado<UserEntity?> {
        val path = StaticNamesPath.passwordGrant.path
        val uri = urlAuth(
            path,
//            mapOf("key" to MyUtils.apiKey)
        )
        val headers = mapOf("Content-Type" to "application/json")
        return httpCall(
            f = { client ->
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = Json.encodeToString(request).toRequestBody(mediaType)
                val httpUrl = uri.getOrNull()
                    ?: throw uri.exceptionOrNull() ?: Exception("Invalid URL")
                val req = Request.Builder()
                    .url(httpUrl)
                    .post(body)
                    .headers(headers.toHeaders())
                    .build()
                client.newCall(req).execute()
            },
            serializer = UserEntity.serializer()
        )
    }

    suspend fun register(request: RegisterEntity): Resultado<UserEntity?> {
        val path = StaticNamesPath.register.path
        val uri = urlAuth(
            path,
//            mapOf("key" to MyUtils.apiKey)
        )
        val headers = mapOf("Content-Type" to "application/json")
        return httpCall(
            f = { client ->
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = Json.encodeToString(request).toRequestBody(mediaType)
                val httpUrl = uri.getOrNull()
                    ?: throw uri.exceptionOrNull() ?: Exception("Invalid URL")
                val req = Request.Builder()
                    .url(httpUrl)
                    .post(body)
                    .headers(headers.toHeaders())
                    .build()
                client.newCall(req).execute()
            },
            serializer = UserEntity.serializer()
        )
    }

    fun closeSession(): Unit {
        cache.emptyCacheData()
    }

    suspend fun getClients(): Resultado<ClientsResponse?> {
        val path = StaticNamesPath.getClients.path
        val uri = url(
            path,
//            mapOf("key" to MyUtils.apiKey)
        )
        val headers = mapOf("Content-Type" to "application/json")
        val httpUrl = uri.getOrNull()
            ?: throw uri.exceptionOrNull() ?: Exception("Invalid URL")
        return httpCall(
            f = { client ->
                val req = Request.Builder()
                    .url(httpUrl)
                    .get()
                    .headers(headers.toHeaders())
                    .build()
                client.newCall(req).execute()
            },
            serializer = ClientsResponse.serializer()
        )
    }

    suspend fun getMessages(queryParameters: Map<String, String?>?): Resultado<List<ReceiveMessageModel>?> {
        val path = StaticNamesPath.getMessages.path
        val uri = url(
            path,
//            mapOf("key" to MyUtils.apiKey)
        )
        val headers = mapOf("Content-Type" to "application/json")
        val httpUrl = uri.getOrNull()
            ?: throw uri.exceptionOrNull() ?: Exception("Invalid URL")
        val finalUrl = httpUrl.newBuilder().apply {
            queryParameters?.forEach { (key, value) ->
                if (!value.isNullOrEmpty()) addQueryParameter(key, value)
            }
        }.build()
        return httpCall(
            f = { client ->
                val req = Request.Builder()
                    .url(finalUrl)
                    .get()
                    .headers(headers.toHeaders())
                    .build()
                client.newCall(req).execute()
            },
            serializer = ListSerializer(ReceiveMessageModel.serializer())
        )
    }

    suspend fun getChats(): Resultado<List<ChatItem>?> {
        val path = StaticNamesPath.getChats.path
        val uri = url(
            path,
//            mapOf("key" to MyUtils.apiKey)
        )
        val request = mapOf("members" to cache.credentialResponse()?._id)
        val headers = mapOf("Content-Type" to "application/json")
        return httpCall(
            f = { client ->
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = Json.encodeToString(request).toRequestBody(mediaType)
                val httpUrl = uri.getOrNull()
                    ?: throw uri.exceptionOrNull() ?: Exception("Invalid URL")
                val req = Request.Builder()
                    .url(httpUrl)
                    .post(body)
                    .headers(headers.toHeaders())
                    .build()
                client.newCall(req).execute()
            },
            serializer = ListSerializer(ChatItem.serializer())
        )
    }
}

// Extension functions and helpers
fun Map<String, String>.toHeaders(): Headers {
    val builder = Headers.Builder()
    this.forEach { (k, v) -> builder.add(k, v) }
    return builder.build()
}