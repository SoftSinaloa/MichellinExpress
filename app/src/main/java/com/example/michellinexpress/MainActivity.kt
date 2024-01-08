package com.example.michellinexpress

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.michellinexpress.Apis.Interface.ApiService
import com.example.michellinexpress.Apis.Models.LoginRequest
import com.example.michellinexpress.Apis.Response.LoginResponse
import com.example.michellinexpress.Views.VistaActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val sharedPreferencesKey = "RememberUser"
    private val sharedPreferencesUsernameKey = "Username"
    private val sharedPreferencesClaveKey = "Password"
    private val sharedPreferencesTokenKey = "Token"
    private lateinit var deviceNameHeaderValue: String
    var tokenok = ""
    private val notificationPermissionRequestCode = 1
    var urlPedido = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        //val textView = findViewById<TextView>(R.id.textView)
        //textView.setBackgroundResource(R.drawable.yellow_border)

        val permissionState =
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        // If the permission is not granted, request it.
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }



        val urlReferer = intent.getStringExtra("UrlReferer") ?: ""
        Log.d("PUSH_Main", "pushTokenMain: $urlReferer")

        if (urlReferer.isNotBlank()) {
             urlPedido = urlReferer
            Log.d("PUSH_Pedido", "pushTokenMainPedido: $urlReferer")

        }

        val textView = findViewById<TextView>(R.id.textView)
        val topBorderDrawable = ContextCompat.getDrawable(this, R.drawable.yellow_top_border)
        textView.background = topBorderDrawable

        //TEXTO CONTRASENA
        val textInputLayout = findViewById<TextInputLayout>(R.id.textInputLayout)
        val passwordEditText = findViewById<TextInputEditText>(R.id.etClave)

// Add password visibility toggle icon using ImageView
        val endIconImageView = ImageView(this)
        endIconImageView.setImageResource(R.drawable.baseline_visibility_off_24) // Set your hidden eye icon
        endIconImageView.setColorFilter(ContextCompat.getColor(this, R.color.blue))
        textInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        textInputLayout.setEndIconDrawable(endIconImageView.drawable) // Use .drawable here
        textInputLayout.isEndIconVisible = true

// Set up password visibility listener
        textInputLayout.setEndIconOnClickListener {
            val currentInputType = passwordEditText.inputType

            if (currentInputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                // Set hidden eye icon
                endIconImageView.setImageResource(R.drawable.baseline_visibility_off_24) // Set your hidden eye icon
            } else {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                // Set visible eye icon
                endIconImageView.setImageResource(R.drawable.baseline_visibility_24) // Set your visible eye icon
            }

            // Explicitly set the drawable for the TextInputLayout
            textInputLayout.setEndIconDrawable(endIconImageView.drawable)
        }






        // Clear the WebView cache

        val rememberUserCheckbox = findViewById<CheckBox>(R.id.chip4)
        val usuario = findViewById<EditText>(R.id.etUser)
        val clavep = findViewById<TextInputEditText>(R.id.etClave)
        val manual = findViewById<TextView>(R.id.textView6)
        val privacidad = findViewById<TextView>(R.id.textView)
        // Check if the user was remembered
        val sharedPreferences = getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val isRemembered = sharedPreferences.getBoolean(sharedPreferencesKey, false)

        if (isRemembered) {
            val rememberedUsername = sharedPreferences.getString(sharedPreferencesUsernameKey, "")
            val rememberedClave = sharedPreferences.getString(sharedPreferencesClaveKey, "")
            usuario.setText(rememberedUsername)
            clavep.setText(rememberedClave)
            rememberUserCheckbox.isChecked = true
        }

        // Obtain IMEI
        deviceNameHeaderValue = getIMEI()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String> ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            tokenok = token
            Log.d("PUSH_TOKEN", "pushToken: $token")
        }


        val btnLogin = findViewById<Button>(R.id.button)
        val btnLogin2 = findViewById<Button>(R.id.button)

        val clave = findViewById<EditText>(R.id.etClave)
        val device =
            "cLOHTXu_SdeuZD3NTlf72u:APA91bHYt3WRlN9-XWpk-zhSoACoovTR3vnFjhQwf_x65lajiwYN59Iu-ds32bP-NoWYHazwukjkfzuQelYfUovcnkvDABHCb0VVCeyjRq4HKvD-jmevD0rwHfzsvKpcdiJO-hMocOIe"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://qa.expresdelivery.softbox.com.mx/handler/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)


        if (urlPedido.isNotBlank()) {
            Log.d("ButtonClick", "Simulating click on btnLogin2")
            //btnLogin2.performClick()
           /* val intent = Intent(this@MainActivity, VistaActivity::class.java)
            intent.putExtra("deviceNameHeaderValue", tokenok)
            intent.putExtra("urlPedido", urlPedido)
            startActivity(intent)*/
            //Aqui empieza login automatico

            val user = usuario.text.toString()
            val password = clave.text.toString()
            val username = usuario.text.toString()

            val loginRequest = LoginRequest(user, password, tokenok.toString())
            Log.d("Token servidor", "TokenServidor: $tokenok")

            val call = apiService.login(loginRequest)

            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.response == 1) {
                            // Save the username if "Remember User" is checked
                            if (rememberUserCheckbox.isChecked) {
                                val editor = sharedPreferences.edit()
                                editor.putBoolean(sharedPreferencesKey, true)
                                editor.putString(sharedPreferencesUsernameKey, username)
                                editor.putString(sharedPreferencesClaveKey, password)
                                editor.putString(sharedPreferencesTokenKey, tokenok.toString())
                                editor.apply()
                            }
                            // Successful login, start a new activity
                            val intent = Intent(this@MainActivity, VistaActivity::class.java)
                            intent.putExtra("deviceNameHeaderValue", tokenok.toString())
                            intent.putExtra("urlPedido", urlPedido)
                            startActivity(intent)
                        } else {
                            // Incorrect user or password, display an error message
                            Toast.makeText(
                                this@MainActivity,
                                "Error de usuario o contraseña",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle other errors, for example, display a generic error message
                        Toast.makeText(
                            this@MainActivity,
                            "Error en la respuesta del servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Handle failure, for example, display a toast or log the error
                    Toast.makeText(
                        this@MainActivity,
                        "Error de conexión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }


        btnLogin.setOnClickListener {
            val user = usuario.text.toString()
            val password = clave.text.toString()
            val username = usuario.text.toString() // Get the text from the EditText

            //val loginRequest = LoginRequest(user, password, deviceNameHeaderValue)
            val loginRequest = LoginRequest(user, password, tokenok.toString())
            Log.d("Token servidor", "TokenServidor: $tokenok")
            val call = apiService.login(loginRequest)

            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.response == 1) {
                            // Save the username if "Remember User" is checked
                            if (rememberUserCheckbox.isChecked) {
                                val editor = sharedPreferences.edit()
                                editor.putBoolean(sharedPreferencesKey, true)
                                editor.putString(sharedPreferencesUsernameKey, username)
                                editor.putString(sharedPreferencesClaveKey, password)
                                editor.putString(sharedPreferencesTokenKey, tokenok.toString())
                                editor.apply()
                            }
                            // Successful login, start a new activity
                            val intent = Intent(this@MainActivity, VistaActivity::class.java)
                            intent.putExtra("deviceNameHeaderValue", tokenok.toString())
                            startActivity(intent)
                        } else {
                            // Incorrect user or password, display an error message
                            Toast.makeText(
                                this@MainActivity,
                                "Error de usuario o contraseña",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle other errors, for example, display a generic error message
                        Toast.makeText(
                            this@MainActivity,
                            "Error en la respuesta del servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Handle failure, for example, display a toast or log the error
                    Toast.makeText(
                        this@MainActivity,
                        "Error de conexión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        btnLogin2.setOnClickListener {
            val user = usuario.text.toString()
            val password = clave.text.toString()
            val username = usuario.text.toString() // Get the text from the EditText

            //val loginRequest = LoginRequest(user, password, deviceNameHeaderValue)
            val loginRequest = LoginRequest(user, password, tokenok)
            Log.d("Token servidor2", "TokenServidor2: $tokenok")
            val call = apiService.login(loginRequest)

            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.response == 1) {
                            // Save the username if "Remember User" is checked
                            if (rememberUserCheckbox.isChecked) {
                                val editor = sharedPreferences.edit()
                                editor.putBoolean(sharedPreferencesKey, true)
                                editor.putString(sharedPreferencesUsernameKey, username)
                                editor.putString(sharedPreferencesClaveKey, password)
                                editor.putString(sharedPreferencesTokenKey, tokenok)
                                editor.apply()
                            }
                            // Successful login, start a new activity
                            val intent = Intent(this@MainActivity, VistaActivity::class.java)
                            intent.putExtra("deviceNameHeaderValue", tokenok.toString())
                            intent.putExtra(urlReferer,urlPedido)
                            startActivity(intent)
                        } else {
                            // Incorrect user or password, display an error message
                            Toast.makeText(
                                this@MainActivity,
                                "Error de usuario o contraseña",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle other errors, for example, display a generic error message
                        Toast.makeText(
                            this@MainActivity,
                            "Error en la respuesta del servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Handle failure, for example, display a toast or log the error
                    Toast.makeText(
                        this@MainActivity,
                        "Error de conexión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        manual.setOnClickListener {
            val websiteUrl = "https://qa.expresdelivery.softbox.com.mx/pdf/manual-acceso-usuarios.pdf"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
            startActivity(intent)
        }
        privacidad.setOnClickListener {
            val websiteUrl = "https://www.michelin.com.mx/politicas-de-privacidad"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
            startActivity(intent)



            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM Token", "Token: $token")
                    // Aquí puedes enviar el token a tu servidor o realizar acciones adicionales
                } else {
                    Log.e("FCM Token", "Error al obtener el token", task.exception)
                }
            }
        }
    }


    private fun getIMEI(): String {
        val telephonyManager =
            getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // If Android version is Q or above, you need special permission
            // or use another method for device identification
            generateRandomString(30)
        } else {
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                telephonyManager.deviceId ?: generateRandomString(30)
            } else {
                // Request the READ_PHONE_STATE permission from the user
                // You should handle the permission request response in onRequestPermissionsResult
                requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE), 1)
                generateRandomString(30)
            }
        }
    }

    private fun generateRandomString(length: Int): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { characters.random() }
            .joinToString("")
    }




    override fun onBackPressed() {
        return
    }
}

