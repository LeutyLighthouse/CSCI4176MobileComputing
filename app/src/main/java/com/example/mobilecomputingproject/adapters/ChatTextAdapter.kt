package com.example.mobilecomputingproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecomputingproject.chat.ChatMessage
import com.example.mobilecomputingproject.MainActivity
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.helpers.Utls

class ChatTextAdapter(context: Context, chat_data: MutableList<ChatMessage>, listener: ChatTextAdapter.ItemClickListener) : RecyclerView.Adapter<ChatTextAdapter.ViewHolder>(){
    private val RECEIVED = 1
    private val SENT = 2

    var message_list = chat_data
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var clickListener: ChatTextAdapter.ItemClickListener = listener

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var msg_text: TextView = itemView.findViewById<TextView>(R.id.chat_contents)
        val msg_time: TextView = itemView.findViewById<TextView>(R.id.time_message_sent)

        fun bind_message_obj_to_view(msg: ChatMessage)
        {
            val time_str: String = Utls.convert_long_time_to_string(msg.time)
            msg_text.setText(msg.contents)
            msg_time.setText(time_str)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var curr_msg: ChatMessage = message_list[position]

        return if (curr_msg.sender.equals(MainActivity.user!!.email!!)) {
            SENT
        } else {
            RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        lateinit var view: View

        if (viewType == SENT)
        {
            view = inflater.inflate(R.layout.sent_msg, parent, false)
            return ViewHolder(view)
        }
        else // viewType == RECEIVED
        {
            view = inflater.inflate(R.layout.received_msg, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var curr_msg: ChatMessage = message_list[position]
        holder.bind_message_obj_to_view(curr_msg)

        holder.msg_text.setOnClickListener {
            clickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int { return message_list.size }

    interface ItemClickListener
    {
        fun onItemClick(position: Int)
    }
}