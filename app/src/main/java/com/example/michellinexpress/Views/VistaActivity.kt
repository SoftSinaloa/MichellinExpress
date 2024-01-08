package com.example.michellinexpress.Views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.michellinexpress.MainActivity
import com.example.michellinexpress.R
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class VistaActivity : AppCompatActivity() {
    private lateinit var deviceNameHeaderValue: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista)
        deviceNameHeaderValue = intent.getStringExtra("deviceNameHeaderValue") ?: ""
        val urlReferer = intent.getStringExtra("urlPedido") ?: ""
        Log.d("PUSH_TOKENentro", "pushTokenEntro: $urlReferer")

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("PUSH_TOKEN", "pushToken: $token")
        }

        val webView: WebView = findViewById(R.id.webView)
        // Clear the WebView cache
        webView.clearCache(true)
        // Enable JavaScript (if needed)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Set up WebView clients
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.d("WebView", "Loading URL: $url")

                if (!urlReferer.isNullOrEmpty()) {
                    // No redirigir si hay una URL específica en urlReferer
                    return false
                }

                // Resto del código para manejar otras redirecciones según tus condiciones actuales
                if (url.equals("http://qa.v2.expresdelivery.softbox.com.mx/LoginServicioMichelin.aspx", ignoreCase = true)) {
                    val intent = Intent(this@VistaActivity, MainActivity::class.java)
                    startActivity(intent)
                    return true
                }
                if (url.equals("https://qa.v2.expresdelivery.softbox.com.mx/LoginServicioMichelin.aspx", ignoreCase = true)) {
                    val intent = Intent(this@VistaActivity, MainActivity::class.java)
                    startActivity(intent)
                    return true
                }

                // Resto del código para manejar otras redirecciones según tus condiciones actuales

                return false
            }
        }

        webView.webChromeClient = WebChromeClient()

        // Load the URL with custom headers
        if (!urlReferer.isNullOrEmpty()) {
            val deviceNameHeader = "DeviceName"
            Log.d("PUSH_TOKEN", "pushToken: tokentro")
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val urlObject = URL(urlReferer)
                    val connection = urlObject.openConnection() as HttpURLConnection
                    connection.setRequestProperty("DeviceName", deviceNameHeaderValue)

                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val content = StringBuilder()
                    var line: String? = reader.readLine()
                    while (line != null) {
                        content.append(line)
                        line = reader.readLine()
                    }

                    // Use the content in the main thread
                    withContext(Dispatchers.Main) {
                        // Load the URL into WebView
                        //webView.loadUrl(urlReferer)
                        Log.d("PUSH_TOKENFINAL", "pushTokenEntroFinal: $urlReferer")

                        val headers = HashMap<String, String>()
                        headers[deviceNameHeader] = deviceNameHeaderValue
                        webView.loadUrl(urlReferer, headers)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            val url = "http://qa.v2.expresdelivery.softbox.com.mx/"
            val deviceNameHeader = "DeviceName"
            //val deviceNameHeaderValue = "cLOHTXu_SdeuZD3NTlf72u:APA91bHYt3WRlN9-XWpk-zhSoACoovTR3vnFjhQwf_x65lajiwYN59Iu-ds32bP-NoWYHazwukjkfzuQelYfUovcnkvDABHCb0VVCeyjRq4HKvD-jmevD0rwHfzsvKpcdiJO-hMocOIe"

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val urlObject = URL(url)
                    val connection = urlObject.openConnection() as HttpURLConnection
                    connection.setRequestProperty(deviceNameHeader, deviceNameHeaderValue)

                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val content = StringBuilder()
                    var line: String? = reader.readLine()
                    while (line != null) {
                        content.append(line)
                        line = reader.readLine()
                    }

                    // Use the content in the main thread
                    withContext(Dispatchers.Main) {
                        // Cargar la URL con encabezados personalizados
                        val headers = HashMap<String, String>()
                        headers[deviceNameHeader] = deviceNameHeaderValue
                        webView.loadUrl(url, headers)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun requestNotificationPermission() {
        // Implement your notification permission logic here
    }
}
