package com.example.myapplication

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Socket(private val endpoint: String = "https://192.168.1.136:8080") {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val client = HttpClient(OkHttp) {
        install(WebSockets)
        install(JsonFeature) {
            serializer = KotlinxSerializer()
            acceptContentTypes = acceptContentTypes + ContentType.Application.Json
        }
    }

    private val request: HttpRequestBuilder.() -> Unit = {
        url {
            takeFrom(endpoint)
            protocol = URLProtocol.WS
        }
    }

    init {
        useClient {
            send("Android socket connected")
            while (true) {
                val message = when (val receive = incoming.receive()) {
                    is Frame.Text -> receive.readText()
                    is Frame.Binary -> receive.readBytes().toString()
                    else -> "unknown frame: $receive"
                }
                Log.i("TEST", message)
            }
        }
    }

    suspend fun send(message: String) = useClient {
        send(Frame.Text(message))
    }

    private fun useClient(block: suspend DefaultClientWebSocketSession.() -> Unit) {
        scope.launch {
            client.webSocket(request, block)
        }
    }
}