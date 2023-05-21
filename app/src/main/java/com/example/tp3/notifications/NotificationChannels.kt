package com.example.tp3.notifications

import android.app.Application

/**
 * Classe pour gérér les channels de notifications
 * Pas utilisé, car crash l'application.
 * Channel enregistré dans MainActivity.kt
 */
class NotificationChannels : Application() {
    override fun onCreate() {
        super.onCreate()
//        createSmsNotificationChannel()
    }

    /**
     * Créer le channel de notification pour les SMS
     */
//    fun createSmsNotificationChannel() {
////        val desc = context.getString(R.string.notification_channel_description) // Crash l'application????
//
//        val channel = NotificationChannel(
//            SmsNotificationService.SMS_CHANNEL_ID,
//            "SMS notifications",
//            NotificationManager.IMPORTANCE_HIGH
//        )
//        channel.description = "SMS notifications"
//
//        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }

}