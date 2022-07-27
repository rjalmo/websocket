package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Call
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val scope = CoroutineScope(Dispatchers.Default)

    val endpoint = Url("http://192.168.1.136:8080")

    private val client = HttpClient(OkHttp) {
        install(WebSockets)
        install(JsonFeature) {
            serializer = KotlinxSerializer()
            acceptContentTypes = acceptContentTypes + ContentType.Any
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWebSocket()
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Greeting("Android")
                    Send()
                }
            }
        }
    }

    private fun setupWebSocket() {
        scope.launch {
//            client.ws("http://192.168.1.136:8080") {
//                send(Frame.Text("Ping to webserver"))
//                println((incoming.receive() as? Frame.Text)?.readText())
//            }
            client.webSocket({
                url {
                    takeFrom(endpoint)
                    protocol = URLProtocol.WS
                }
            }) {
                send(Frame.Text("Ping to webserver"))
                //Do something
            }
        }
    }
}

@Composable
fun Send() {
    val coroutineScope = rememberCoroutineScope()
    IconButton(onClick = {
        coroutineScope.launch {
            // TODO send
        }
    }) {
        Icon(Icons.TwoTone.Call, contentDescription = "Load")
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
//        Greeting("asdf")
        Send()
    }
}