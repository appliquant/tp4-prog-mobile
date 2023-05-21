package com.example.tp3.db

import android.app.Application
import com.example.tp3.notifications.NotificationChannels

/**
 * Classe application applée dès le lancement de l'application
 */
class MessageApplication : Application() {
    /**
     * Instance base de données
     */
    val database: MessageDatabase by lazy { MessageDatabase.getInstance(this) }

    override fun onCreate() {
        super.onCreate()

        // Initialiser service de notification pour les SMS
        NotificationChannels()
    }

}