package com.example.tp3.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.util.Log
import com.example.tp3.db.Message
import com.example.tp3.notifications.SmsNotificationService
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.SecureRandom

/**
 * Classe qui reçoit les SMS
 */
class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "android.provider.Telephony.SMS_RECEIVED" -> {
                val sms = getMessagesFromIntent(intent)

                for (msg in sms) {
                    Log.d("SmsReceiver", "SMS_RECEIVED: ${msg.displayMessageBody}")

                    // Message doit respecter le format suivant: "nom, prénom, message, lat, lng"
                    val message = msg.displayMessageBody.split(",")
                    if (message.size != 5) {
                        return
                    }

                    val random = SecureRandom()

                    val id = random.nextLong()
                    val firstname = message[0]
                    val lastname = message[1]
                    val msgContent = message[2]
                    val picture = "https://robohash.org/$firstname$lastname"
                    val latitude = message[3].toDouble()
                    val longitude = message[4].toDouble()

                    // Message
                    val newMessage = Message(
                        id, firstname, lastname,
                        msgContent, picture, latitude, longitude
                    )

                    // Enregistrer le message dans firestore
                    saveMessageFirestore(
                        newMessage, callbackNotificationSuccess = {
                            // Envoyer la notification de succès
                            val service = SmsNotificationService(context!!)
                            service.showNotificationSuccess()
                        },

                        callbackNotificationError = {
                            // Envoyer la notification d'erreur
                            val service = SmsNotificationService(context!!)
                            service.showNotificationError()
                        }
                    )

                }
            }
        }

    }

    /**
     * Enregistrer le message dans firestore
     */
    private fun saveMessageFirestore(
        message: Message,
        callbackNotificationSuccess: () -> Unit,
        callbackNotificationError: () -> Unit
    ) {
        val fireStoreDb = Firebase.firestore
        val collectionRef = fireStoreDb.collection("messages")
        collectionRef.add(message)
            .addOnSuccessListener {
                // Envoyer la notification de succès
                callbackNotificationSuccess()
            }
            .addOnFailureListener {
                // Envoyer la notification d'erreur
                callbackNotificationError()
            }
    }
}

