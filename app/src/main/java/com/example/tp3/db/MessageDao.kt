package com.example.tp3.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages")
    fun getAllMessages(): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMessages(messages: List<Message>)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}