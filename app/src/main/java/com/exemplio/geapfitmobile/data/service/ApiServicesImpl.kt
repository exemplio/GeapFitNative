package com.exemplio.geapfitmobile.data.service

import ClientDocument
import ClientsResponse
import ErrorResponse
import HttpUtil
import android.util.Log
import com.exemplio.geapfitmobile.utils.CacheService
import com.exemplio.geapfitmobile.domain.entity.PasswordGrantEntity
import com.exemplio.geapfitmobile.data.model.Resultado
import com.exemplio.geapfitmobile.domain.entity.VerifyPasswordResponse
import com.exemplio.geapfitmobile.utils.MyUtils
import com.geapfit.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

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
        val isOnline = isOnlineProvider.isOnline()
        return try {
            if (isOnline) {
                val builder = HttpUrl.Builder()
                    .scheme("https")
                    .host(MyUtils.baseAuth)
                    .addEncodedPathSegments(unencodedPath)
                queryParameters?.forEach { (key, value) ->
                    builder.addQueryParameter(key, value.toString())
                }
                println("Http URL: ${builder.build()}")
                Result.success(builder.build())
            }
            else {
                println("isOnline3")
                Result.failure(IOException("No posee conexión a internet"))
            }
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
                println("RESPONSE apiServicesImpl: $response")
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

    suspend fun passwordGrant(request: PasswordGrantEntity): Resultado<VerifyPasswordResponse?> {
        val path = StaticNamesPath.passwordGrant.path
        val uri = urlAuth(
            path,
            mapOf("key" to MyUtils.apiKey)
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
            serializer = VerifyPasswordResponse.serializer()
        )
    }

//    suspend fun resendSign(param: CredentialEntity): Result<Any> {
//        return try {
//            val params = mapOf("app-id" to MyUtils.clientId, "email" to param)
//            val headers = mapOf(
//                "Content-Type" to "application/json",
//                "Accept" to "application/json",
//                "app-id" to MyUtils.clientId
//            )
//            val uri = url(
//                "${MyUtils.type}${MyUtils.type}${StaticNamesPath.recover.path}",
//                params
//            )
//            val httpUrl = uri.getOrNull()
//                ?: return Result.failure(uri.exceptionOrNull() ?: Exception("Invalid URL"))
//            val response = httpCall(
//                f = { client ->
//                    val req = Request.Builder()
//                        .url(httpUrl)
//                        .get()
//                        .headers(headers.toHeaders())
//                        .build()
//                    client.newCall(req).execute()
//                },
//                parseJson = { json -> json }
//            )
//            Result.success(response)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

//    suspend fun recoveryQuestions(param: String): Result<Any> {
//        val params = mapOf("app-id" to MyUtils.clientId, "email" to param)
//        val headers = mapOf(
//            "Content-Type" to "application/json",
//            "Accept" to "application/json",
//            "app-id" to MyUtils.clientId
//        )
//        val uri = url(
//            "${MyUtils.type}${MyUtils.type}${StaticNamesPath.recover.path}",
//            params
//        )
//        val httpUrl = uri.getOrNull()
//            ?: return Result.failure(uri.exceptionOrNull() ?: Exception("Invalid URL"))
//        return try {
//            val response = httpCall(
//                f = { client ->
//                    val req = Request.Builder()
//                        .url(httpUrl)
//                        .get()
//                        .headers(headers.toHeaders())
//                        .build()
//                    client.newCall(req).execute()
//                },
//                parseJson = { json -> json }
//            )
//            Result.success(response)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

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
}

// Extension functions and helpers
fun Map<String, String>.toHeaders(): Headers {
    val builder = Headers.Builder()
    this.forEach { (k, v) -> builder.add(k, v) }
    return builder.build()
}