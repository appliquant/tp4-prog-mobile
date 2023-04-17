package com.example.tp3.googlemap

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.tp3.R
import com.example.tp3.db.Message
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso

/**
 * Classe qui permet de customiser la fenêtre d'info des markers.
 * Utilise le layout marker_info_contents.xml.
 * Utilisé par MapFragment.
 * @param context Contexte de l'application
 */
class MarkerInfoWindowAdapter(
    private val context: Context,
) : GoogleMap.InfoWindowAdapter {
    /**
     * Customiser la fenêtre d'info
     */
    override fun getInfoWindow(marker: Marker?): View {
        val message = marker?.tag as? Message ?: return View(context)
        val view = LayoutInflater.from(context).inflate(R.layout.marker_info_contents, null)

        /**
         * Remplir la vue avec les champs du message
         */
        // Le message
        view.findViewById<TextView>(R.id.marker_info_contents_message).text = message.message

        // Image (avec picasso)
        Picasso.get().load(message.picture)
            .into(view.findViewById<ImageView>(R.id.marker_info_contents_avatar))

        return view
    }

    /**
     * Customiser le contenu de la fenêtre d'info
     */
    override fun getInfoContents(marker: Marker?): View {
        return View(context)
    }
}