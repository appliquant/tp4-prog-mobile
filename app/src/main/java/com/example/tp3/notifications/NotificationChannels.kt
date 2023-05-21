package com.example.tp3.notifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.tp3.R

/**
 * Classe pour gérér les channels de notifications
 */
class NotificationChannels : Application() {
    override fun onCreate() {
        super.onCreate()
        createSmsNotificationChannel()
    }

    /**
     * Créer le channel de notification pour les SMS
     */
    private fun createSmsNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SmsNotificationService.SMS_CHANNEL_ID,
                getString(R.string.notification_channel_description),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = getString(R.string.notification_channel_description)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}