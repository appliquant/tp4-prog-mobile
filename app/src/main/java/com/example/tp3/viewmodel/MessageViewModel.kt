package com.example.tp3.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tp3.db.MessageDao
import com.example.tp3.http.CommentsApi
import com.example.tp3.http.CommentsApiService
import kotlinx.coroutines.launch

class MessageViewModel(private val messageDao: MessageDao) : ViewModel() {

    init {
        getDefaultMessages()
    }

    /**
     * Récupérer les messages par défaut du serveur
     */
    fun getDefaultMessages() {
        viewModelScope.launch {
            // TODO: Vérifier si la liste est vide

            try {
                val defaultMessages = CommentsApi.retrofitService.getMessages()
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

