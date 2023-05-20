package com.example.tp3.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms.Intents.getMessagesFromIntent
import android.util.Log
import android.widget.Toast
import com.example.tp3.R
import com.example.tp3.db.Message
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.SecureRandom

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

                    // Enregistrer le message dans firestore
                    saveMessageFirestore(
                        Message(
                            id,
                            firstname,
                            lastname,
                            msgContent,
                            picture,
                            latitude,
                            longitude
                        ), context
                    )

                }
            }
        }
    }

    /**
     * Enregistrer le message dans firestore
     */
    private fun saveMessageFirestore(message: Message, context: Context?) {
        val fireStoreDb = Firebase.firestore
        val collectionRef = fireStoreDb.collection("messages")
        collectionRef.add(message)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    context?.getString(R.string.message_received_sms_added),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    context?.getString(R.string.message_received_sms_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}

