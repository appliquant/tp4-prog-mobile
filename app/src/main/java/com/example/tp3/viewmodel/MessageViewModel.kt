package com.example.tp3.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.tp3.db.Message
import com.example.tp3.db.MessageDao
import com.example.tp3.http.CommentsApi
import com.example.tp3.http.CommentsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MessageViewModel(private val messageDao: MessageDao) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()

    /**
     * Messages dans la base de données
     */
//    val messages: LiveData<List<Message>> = _messages

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
    suspend fun insertAllMessages(messages: List<Message>) = messageDao.insertAllMessages(messages)


    init {
        getDefaultMessages()
    }

    /**
     * Récupérer les messages par défaut du serveur et initialiser la valeur de `messages`
     */
    fun getDefaultMessages() {
        viewModelScope.launch {

            try {
                // Récupérer les messages par défaut du serveur
                val defaultMessages = CommentsApi.retrofitService.getMessages()

                // Vérifier si la base de données est vide & insérer les messages par défaut
                val nbMessages = getCountMessages()
                nbMessages.collect {
                    if (it <= 0) {
                        insertAllMessages(defaultMessages)
                    }
                }

//                _messages.value = getAllMessages().asLiveData().value

                Log.d("MessageViewModel", "getDefaultMessages: $defaultMessages")
            } catch (err: Exception) {
                Log.e("MessageViewModel", "getDefaultMessages: $err")
            }
        }
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

