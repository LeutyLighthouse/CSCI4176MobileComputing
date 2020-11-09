package com.example.mobilecomputingproject.adapters

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView

import com.example.mobilecomputingproject.R

class ChatSelectorAdapter(context: Context, data: MutableList<String>, listener: ItemClickListener) : RecyclerView.Adapter<ChatSelectorAdapter.ViewHolder>() {

    private var names = data
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var clickListener: ItemClickListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.chat_selector_list_row, parent, false)
        return ViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: ChatSelectorAdapter.ViewHolder, position: Int) {
        var name: String = names[position]
        holder.nameTextView!!.text = name
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int { return names.size }

    inner class ViewHolder(itemView: View, listener: ItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        var nameTextView = itemView.findViewById<TextView>(R.id.chatTitle)
    }

    interface ItemClickListener
    {
        fun onItemClick(position: Int)
    }

}