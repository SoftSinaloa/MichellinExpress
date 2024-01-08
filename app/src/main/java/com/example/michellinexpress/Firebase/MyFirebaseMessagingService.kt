package com.example.michellinexpress.Firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.michellinexpress.MainActivity
import com.example.michellinexpress.R
import com.example.michellinexpress.Views.VistaActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val CHANNEL_ID = "NOTIFICATION_CHANNEL"
const val  CHANNEL_NAME = "com.example.michellinexpress"

class MyFirebaseMessagingService : FirebaseMessagingService() {

  /*  override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle the received message
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            // Show a notification
            generateNotification(it.title, it.body)
        }
    }
*/

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a data payload
        remoteMessage.data.isNotEmpty().let {
            // Extract the URL from the data payload
            val urlReferer = remoteMessage.data["UrlReferer"]

            // Check if the URL is not null or empty
            if (!urlReferer.isNullOrEmpty()) {
                // Open the VistaActivity with the URL
                //val intent = Intent(this, VistaActivity::class.java)
                Log.d("Token clasefirebase", "TokenServidorfirebase: $urlReferer")
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("UrlReferer", urlReferer)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }


    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        // Guardar el nuevo token en tu base de datos o en SharedPreferences
        saveTokenToDatabase(newToken)
    }

    private fun generateNotification(title: String?, message: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        // channel id, channel name
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        // Attach la notificación creada a un layout personalizado
        builder.setContent(getRemoteView(title, message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Verificar si Android es mayor a Android Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())
    }

    private fun saveTokenToDatabase(token: String) {
        // Aquí puedes implementar la lógica para guardar el token en tu base de datos
        // o en SharedPreferences según tus necesidades
        // Por ejemplo, puedes almacenarlo en SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("FCMToken", token)
        editor.apply()

        Log.d(TAG, "Token saved to preferences: $token")
    }

    private fun getRemoteView(title: String?, message: String?): RemoteViews {
        val remoteView = RemoteViews("com.example.michellinexpress", R.layout.notifcation)
        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.image, R.drawable.ic_notifaction)

        return remoteView
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
