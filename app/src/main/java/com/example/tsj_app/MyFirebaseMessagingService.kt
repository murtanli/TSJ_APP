package com.example.tsj_app
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Логируем данные уведомления
        Log.d("FCM", "From: ${remoteMessage.from}")
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Title: ${it.title}")
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }

        // Показываем уведомление
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        // ID канала уведомлений
        val channelId = "default_channel"

        // Получаем NotificationManager для показа уведомления
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Для Android 8.0 и выше создаём канал уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Строим уведомление
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title ?: "Новое уведомление")
            .setContentText(message ?: "У вас новое уведомление.")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Иконка уведомления
            .setAutoCancel(true) // Удаляет уведомление при клике
            .build()

        // Показываем уведомление с ID 0 (можно использовать уникальные ID для разных уведомлений)
        notificationManager.notify(0, notification)
    }
}
