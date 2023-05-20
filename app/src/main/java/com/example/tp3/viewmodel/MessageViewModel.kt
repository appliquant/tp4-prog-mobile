package com.example.tp3.viewmodel

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tp3.db.Message
import com.example.tp3.db.MessageDao
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MessageViewModel(private val messageDao: MessageDao) : ViewModel() {

    // ============================================================================
    // Variables
    // ============================================================================
    private val _currentUser = MutableLiveData<FirebaseUser>()

    /**
     * Utilisateur courant
     */
    val currentUser: MutableLiveData<FirebaseUser> = _currentUser


    // ============================================================================
    // Fonctions DAO
    // ============================================================================
    /**
     * Récupérer le count des messages
     */
    private fun getCountMessages() = messageDao.getCount()

    /**
     * Récupérer tous les messages de la base de données
     */
    fun getAllMessages() = messageDao.getAllMessages()

    /**
     * Insérer un message dans la base de données
     */
    suspend fun insertMessage(message: Message) = messageDao.insertMessage(message)

    /**
     * Insérer tous les messages dans la base de données
     */
    private suspend fun insertAllMessages(messages: List<Message>) =
        messageDao.insertAllMessages(messages)

    /**
     * Supprimer tous les messages
     */
    suspend fun deleteAllMessages() = messageDao.deleteAllMessages()

    // ============================================================================
    // Fonctions authentification
    // ============================================================================
    /**
     * Sauvegarder l'utilisateur dans la base de données
     */
    fun setUser(user: FirebaseUser) {
        _currentUser.value = user
    }

    // ============================================================================
    // Fonctions firebase
    // ============================================================================
    /**
     * Récupérer les messages de firestore
     * @return Liste des messages
     */
//    private fun _getMessagesFromFirestore(): List<Message> {
//        val fireStoreDb = Firebase.firestore
//        // Convertir les documents en objets Message (cast)
//        var messages = mutableListOf<Message>()
//        val sucessCallback : (QuerySnapshot) -> Unit = { result ->
//            for (document in result) {
//                val m = Message(
//                    document.data["id"] as Long,
//                    document.data["firstname"] as String,
//                    document.data["lastname"] as String,
//                    document.data["message"] as String,
//                    document.data["picture"] as String,
//                    document.data["latitude"] as Double,
//                    document.data["longitude"] as Double,
//                )
//
//                messages.add(m)
//            }
//
//            return messages
//        }
//
//        fireStoreDb
//            .collection("messages")
//            .get()
//            .addOnSuccessListener(sucessCallback)
//            .addOnFailureListener {
//                Log.d("MessageViewModel", "Erreur firestore: ", it)
//            }
//
//        return sucessCallback
//    }
//

    // ============================================================================
    // Constructeur
    // ============================================================================
    init {
        // Récupérer les messages de firestore
        getMessagesFromFirestoreRealtime()
    }

    // ============================================================================
    // Fonctions
    // ============================================================================

    /**
     * Récupérer les messages de firestore en temps réel
     * et les sauvegarder dans la base de donnée locale
     */
    private fun getMessagesFromFirestoreRealtime() {

        try {
            val fireStoreDb = Firebase.firestore
            val collectionRef = fireStoreDb.collection("messages")
            collectionRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("MessageViewModel", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {

                    // Convertir les documents en objets Message (cast)
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
                    Log.d("MessageViewModel", "Current data: null")

                }
            }

        } catch (err: Exception) {
            Log.e("MessageViewModel", "getDefaultMessages: $err")
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
            return MessageViewModel(_messageDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

