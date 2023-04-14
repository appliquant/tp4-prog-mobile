package com.example.tp3.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tp3.databinding.MessagesListItemBinding
import com.example.tp3.db.Message
import com.squareup.picasso.Picasso

class MessageAdapter : ListAdapter<Message, MessageAdapter.ViewHolder>(DiffCallback) {
    /**
     * Référence vers un élément unique du recycler view
     */
    class ViewHolder(private var binding: MessagesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Binding de `res/layout/message_list_item`

        // Fonction utilisé pour passer les éléments de onBindViewHolder
        fun bind(message: Message) {
            binding.messagesListImageFullname.text = "${message.firstname} ${message.lastname}"
            binding.messagesListImageMessage.text = message.message
            Picasso.get().load(message.picture).into(binding.messagesListImageAvatar)
        }
    }

    /**
     * Créer des nouvelles vues
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            MessagesListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        return viewHolder
    }

    /**
     * Remplacer le contenu d'une vue d'élément de liste
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    /**
     * Objet qui aide ListAdapter à identifier les éléments qui ont été
     * modifiés entre l'ancienne liste et la nouvelle après une mise à jour
     */
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }
        }
    }

}