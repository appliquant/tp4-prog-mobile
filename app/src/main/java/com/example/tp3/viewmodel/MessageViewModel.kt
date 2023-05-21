package com.example.tp3.viewmodel

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tp3.db.Message
import com.example.tp3.db.MessageDao
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MessageViewModel(
    /**
     * DAO pour les messages
     */
    private val messageDao: MessageDao
) : ViewModel() {

    // ============================================================================
    // Fonctions DAO
    // ============================================================================
    /**
     * Récupérer tous les messages de la base de données
     */
    fun getAllMessages() = messageDao.getAllMessages()

    /**
     * Insérer tous les messages dans la base de données locale
     */
    private suspend fun insertAllMessages(messages: List<Message>) =
        messageDao.insertAllMessages(messages)

    // ============================================================================
    // Fonctions firebase
    // ============================================================================

    /**
     * Insérer un message dans firestore
     */
    fun insertMessageFirestore(message: Message) {
        try {
            val fireStoreDb = Firebase.firestore
            val collectionRef = fireStoreDb.collection("messages")
            collectionRef.add(message)
        } catch (err: Exception) {
            Log.e("MessageViewModel", "addMessageToFirebase err: $err")
        }
    }

    // ============================================================================
    // Constructeur
    // ============================================================================
    init {
        getMessagesFirestore()
    }

    // ============================================================================
    // Fonctions
    // ============================================================================

    /**
     * Récupérer les messages de firestore en temps réel
     * et les sauvegarder dans la base de donnée locale
     */
    private fun getMessagesFirestore() {

        try {
            val fireStoreDb = Firebase.firestore
            val collectionRef = fireStoreDb.collection("messages")

            collectionRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {

                    // Convertir les documents en objets Message
                    val messages = mutableListOf<Message>()

                    for (document in snapshot) {
                        val m = Message(
                            document.data["id"] as Long,
                            document.data["firstname"] as String,
                            document.data["lastname"] as String,
                            document.data["message"] as String,
                            document.data["picture"] as String,
                            document.data["latitude"] as Double,
                            document.data["longitude"] as Double,
                        )
                        messages.add(m)
                    }


                    // Insérer les messages dans la base de données
                    viewModelScope.launch {
                        insertAllMessages(messages)
                    }

                } else {
                    Log.d("MessageViewModel", "Data : null")
                }
            }

        } catch (err: Exception) {
            Log.e("MessageViewModel", "getDefaultMessages err : $err")
        }
    }

    /**
     * Companion contenant des constantes et des fonctions globales
     */
    companion object {
        /**
         * Clé pour le DataStore
         */
        private const val CONFIGURATION_DATA_STORE_KEY =
            "COM.EXAMPLE.TP3@CONFIGURATION_DATE_STORE_KEY"

        /**
         * Clé pour le firstname dans le DataStore
         */
        const val CONFIGURATION_DATA_STORE_KEY_FIRSTNAME =
            "COM.EXAMPLE.TP3@CONFIGURATION_DATE_STORE_KEY@FIRSTNAME"

        /**
         * Clé pour le lastname dans le DataStore
         */
        const val CONFIGURATION_DATA_STORE_KEY_LASTNAME =
            "COM.EXAMPLE.TP3.CONFIGURATION_DATE_STORE_KEY@LASTNAME"

        /**
         * Firstname de l'utilisateur par défaut
         */
        const val DEFAULT_FIRSTNAME = "Garneau"

        /**
         * Lastname de l'utilisateur par défaut
         */
        const val DEFAULT_LASTNAME = "Cégep"

        /**
         * DataStore global.
         * Datastore global singleton (utilisé par tous les fragments).
         */
        val Context.GlobalDataStore: DataStore<Preferences> by preferencesDataStore(name = CONFIGURATION_DATA_STORE_KEY)
    }
}

/**
 * Factory pour la création d'un MessageViewModel
 */
class MessageViewModelFactory(private val _messageDao: MessageDao) : ViewModelProvider.Factory {
    /**
     * Créer un MessageViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageViewModel(_messageDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

