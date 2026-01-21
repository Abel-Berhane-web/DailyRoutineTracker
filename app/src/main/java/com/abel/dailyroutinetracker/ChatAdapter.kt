package com.abel.dailyroutinetracker

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardMessage: MaterialCardView = view.findViewById(R.id.cardChatMessage)
        val textMessage: TextView = view.findViewById(R.id.textChatMessage)
        val layoutContainer: LinearLayout = view as LinearLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.textMessage.text = message.text

        val context = holder.itemView.context
        val params = holder.cardMessage.layoutParams as LinearLayout.LayoutParams

        if (message.isUser) {
            holder.layoutContainer.gravity = Gravity.END
            holder.cardMessage.setCardBackgroundColor(ContextCompat.getColor(context, R.color.accent_color))
            holder.textMessage.setTextColor(ContextCompat.getColor(context, R.color.background_color))
            params.marginStart = 64 // Indent from left
            params.marginEnd = 0
        } else {
            holder.layoutContainer.gravity = Gravity.START
            holder.cardMessage.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_background))
            holder.textMessage.setTextColor(ContextCompat.getColor(context, R.color.text_color_primary))
            params.marginStart = 0
            params.marginEnd = 64 // Indent from right
        }
        holder.cardMessage.layoutParams = params
    }

    override fun getItemCount(): Int = messages.size
}