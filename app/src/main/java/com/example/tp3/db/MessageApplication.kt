package com.example.tp3.db

import android.app.Application

class MessageApplication : Application() {
    val database : MessageDatabase by lazy { MessageDatabase.getInstance(this)}
}