package com.example.tp3.notifications

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.tp3.MainActivity
import com.example.tp3.R
import com.example.tp3.ui.ListFragment

/**
 * Service de notification pour les SMS
 */
class SmsNotificationService(
    /**
     * Le contexte
     */
    private val context: Context
) {
    /**
     * Afficher notification de succès
     */
    @SuppressLint("MissingPermission")
    fun showNotificationSuccess() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, SMS_CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_map_black)
            .setContentTitle(context.getString(R.string.message_received))
            .setContentText(context.getString(R.string.message_received_sms_added))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Action de la notification
            .setContentIntent(pendingIntent)
            // Fermer la notification quand on clique dessus
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        with(NotificationManagerCompat.from(context)) {
            notify(SMS_NOTIFICATION_ID_SUCCESS, builder.build())
        }
    }

    /**
     * Afficher notification erreur
     */
    @SuppressLint("MissingPermission")
    fun showNotificationError() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, SMS_CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_map_black)
            .setContentTitle(context.getString(R.string.message_received))
            .setContentText(context.getString(R.string.message_received_sms_failed))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Action de la notification
            .setContentIntent(pendingIntent)
            // Fermer la notification quand on clique dessus
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        with(NotificationManagerCompat.from(context)) {
            notify(SMS_NOTIFICATION_ID_ERROR, builder.build())
        }
    }

    companion object {
        /**
         * Id du channel de notification sms
         */
        const val SMS_CHANNEL_ID = "channel1"
        const val SMS_NOTIFICATION_ID_SUCCESS = 1
        const val SMS_NOTIFICATION_ID_ERROR = 2

    }
}