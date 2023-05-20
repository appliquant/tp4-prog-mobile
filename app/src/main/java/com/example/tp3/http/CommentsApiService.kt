package com.example.tp3.http

import com.example.tp3.db.Message
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.security.SecureRandom


private const val API_URL = "https://kalanso.top"

private val moshiConfig = Moshi.Builder().add(MessageFromApiToMessageForDb()).build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(
        MoshiConverterFactory.create(moshiConfig)
    )
    .baseUrl(API_URL)
    .build()

/**
 * Interface permettant de passer des requêtes à l'API
 */
interface CommentsApiService {
    /**
     * Récupère la liste des messages
     */
    @GET("data.json")
    suspend fun getMessages(): List<Message>
}

/**
 * Objet permettant de récupérer les données de l'API
 */
object CommentsApi {
    val retrofitService: CommentsApiService by lazy { retrofit.create(CommentsApiService::class.java) }
}

/**
 * Convertisseur de données pour Moshi.
 * Converti le shéma de l'API en Message pour la base de données (MessageFromApi -> Message)
 */
class MessageFromApiToMessageForDb {

    /**
     * Converti le schéma de l'API en Message pour la base de données (MessageFromApi -> Message)
     */
    @FromJson
    fun fromJson(data: MessageFromApi): Message {
        val random = SecureRandom()
        val id = random.nextLong()

        return Message(
            id = id,
            firstname = data.firstname,
            lastname = data.lastname,
            picture = data.picture,
            latitude = data.latitude.toDouble(),
            longitude = data.longitude.toDouble(),
            message = data.message,
        )
    }

    /**
     * Converti le Message de la base de données en schéma de l'API (Message -> MessageFromApi)
     */
    @ToJson
    fun toJson(data: Message): MessageFromApi {
        return MessageFromApi(
            isActive = true,
            firstname = data.firstname,
            lastname = data.lastname,
            picture = data.picture,
            latitude = data.latitude.toString(),
            longitude = data.longitude.toString(),
            message = data.message,
        )
    }


}

/**
 * Shéma de l'API
 */
@JsonClass(generateAdapter = true)
data class MessageFromApi(
    val isActive: Boolean,
    val firstname: String,
    val lastname: String,
    val picture: String,
    val latitude: String,
    val longitude: String,
    val message: String,
)