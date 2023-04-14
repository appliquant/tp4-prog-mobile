package com.example.tp3.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class représentant un message
 */
@Entity(tableName = "messages")
data class Message(
    /**
     * Id de l'endroit, auto-généré
     */
    @PrimaryKey(autoGenerate = true) val id: Long,

    /**
     * Prénom
     */
    @ColumnInfo(name = "firstname") val firstname: String,

    /**
     * Nom
     */
    @ColumnInfo(name = "lastname") val lastname: String,

    /**
     * Message
     */
    @ColumnInfo(name = "message") val message: String,

    /**
     * Picture
     */
    @ColumnInfo(name = "picture") val picture: String,

    /**
     * Latitude
     */
    @ColumnInfo(name = "latitude") val latitude: Double,

    /**
     * Longitude
     */
    @ColumnInfo(name = "longitude") val longitude: Double,
) {
//    /**
//     * Id de l'endroit, auto-généré
//     * Non placé dans le constructeur pour éviter de le passer en paramètre
//     */
//    @PrimaryKey(autoGenerate = true)
//    var id: Int? = null
}