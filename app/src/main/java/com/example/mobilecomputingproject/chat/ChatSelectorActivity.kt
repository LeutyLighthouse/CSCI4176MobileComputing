package com.example.mobilecomputingproject.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecomputingproject.MainActivity
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.adapters.ChatSelectorAdapter


class ChatSelectorActivity : AppCompatActivity(), ChatSelectorAdapter.ItemClickListener {

    lateinit var adapter: ChatSelectorAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_selector)

        var recyclerView = findViewById<RecyclerView>(R.id.chat_selector_list)
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        var itemDeco =  DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(itemDeco)

        adapter = ChatSelectorAdapter(this, MainActivity.friends, this)
        recyclerView.adapter = adapter
    }


    override fun onItemClick(position: Int) {
        // set up chat info
        ChatActivity.curr_chat = MainActivity.chats[position]
        ChatActivity.friend_name = MainActivity.friends[position]

        val intent = Intent(applicationContext, ChatActivity::class.java)
        startActivity(intent)
    }

}