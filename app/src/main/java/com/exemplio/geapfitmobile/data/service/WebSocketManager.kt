package com.exemplio.geapfitmobile.data.service

import com.exemplio.geapfitmobile.data.model.Resultado
import com.exemplio.geapfitmobile.utils.CacheService
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okhttp3.*

enum class ConnectionState { Disconnected, Connecting, Connected, Closing, Closed, Failed }

class WebSocketManager(
    private val client: OkHttpClient = OkHttpClient(),
    cache: CacheService
) {
    private var webSocket: WebSocket? = null

    private val _incoming = MutableSharedFlow<String>(
        replay = 0, extraBufferCapacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val incoming = _incoming.asSharedFlow()

    val state = MutableStateFlow(ConnectionState.Disconnected)
    val lastError = MutableStateFlow<Throwable?>(null)
//    private val url = "wss://express-mongo-cobq.onrender.com/ws?userId=${cache.credentialResponse()?._id}"
    private val url = "ws://192.168.0.108:3000/ws?userId=${cache.credentialResponse()?._id}"

    fun <T> decodeMessage(json: String, serializer: KSerializer<T>): Resultado<T?> {
        return try {
            println("WebsocketManager.decodeMessage: ${Json.decodeFromString(serializer, json)}")
            Resultado.success(obj=Json.decodeFromString(serializer, json))
        } catch (e: Exception) {
            Resultado.failMsg("Ocurrió un error inesperado $e", error = 0)
        }
    }

    fun connect(headers: Map<String, String> = emptyMap()) {
        println("Connecting to WebSocket at $url")
        if (state.value == ConnectionState.Connected || state.value == ConnectionState.Connecting) return
        print("Connecting to WebSocket at $url with headers: $headers")
        state.value = ConnectionState.Connecting
        lastError.value = null
        val reqBuilder = Request.Builder().url(url)
        println("WebSocket reqBuilder URL: $reqBuilder")
        headers.forEach { (k, v) -> reqBuilder.addHeader(k, v) }
        val request = reqBuilder.build()
        println("WebSocket request: $request")
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                println("WebSocket connected: $response")
                val helloJson = """{"type":"receive","chat":"689818117bd6a653310456a0"}"""
                webSocket?.send(helloJson)
                state.value = ConnectionState.Connected
            }

            override fun onMessage(ws: WebSocket, text: String) {
                println("WebSocket received message: $text")
                _incoming.tryEmit(text)
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                println("WebSocket closing: $code / $reason")
                state.value = ConnectionState.Closing
                ws.close(code, reason)
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                println("WebSocket closed: $code / $reason")
                state.value = ConnectionState.Closed
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                println("WebSocket failure: ${t.message} / $response")
                lastError.value = t
                state.value = ConnectionState.Failed
            }
        })
    }

    fun send(text: String): Boolean {
        val ok = webSocket?.send(text) ?: false
        if (!ok) lastError.value = IllegalStateException("WebSocket not connected")
        return ok
    }

    fun close(code: Int = 1000, reason: String = "Normal Closure") {
        state.value = ConnectionState.Closing
        webSocket?.close(code, reason)
        webSocket = null
        state.value = ConnectionState.Closed
    }
}