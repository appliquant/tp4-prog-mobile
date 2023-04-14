package com.example.tp3.db

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors


@Database(
    entities = [Message::class],
    version = 1,
    exportSchema = true
)
abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        fun getInstance(context: Context): MessageDatabase {

            val instance = Room.databaseBuilder(
                context, MessageDatabase::class.java,
                "message_database"
            )
                .addCallback(callback = object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }
                })
                .build()

            return instance
        }
    }

}