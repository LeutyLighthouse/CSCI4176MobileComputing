package com.example.mobilecomputingproject.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecomputingproject.MainActivity
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.adapters.ChatTextAdapter
import com.example.mobilecomputingproject.helpers.Utls
import com.example.mobilecomputingproject.interfaces.ICallback
import com.google.firebase.database.DataSnapshot
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ChatActivity : AppCompatActivity() {

    companion object
    {
        lateinit var curr_chat: JSONObject
        var messages = mutableListOf<ChatMessage>()
        lateinit var friend_name: String
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var rv = findViewById<RecyclerView>(R.id.chat_recycler)
        var layoutManager = LinearLayoutManager(this)
        rv.layoutManager = layoutManager
        var adapter = ChatTextAdapter(this, messages)
        rv.adapter = adapter


        var callback = object : ICallback {
            override fun callback(data: DataSnapshot?) {
                var data_str: String = data?.value.toString()

                var json_obj: JSONObject = JSONObject(data_str)
                var json_list: JSONArray = json_obj.getJSONArray(ChatMessage.CHAT)

                messages.clear()
                for (i in 0 until json_list.length())
                {
                    var curr_obj: JSONObject = json_list.getJSONObject(i)
                    messages.add(
                        ChatMessage(curr_obj.getString(ChatMessage.MESSAGE),
                            curr_obj.getString(ChatMessage.SENDER),
                            curr_obj.getString(ChatMessage.TIME).toLong())
                    )
                }
                messages.sort()
                adapter.notifyDataSetChanged()

                rv.scrollToPosition(messages.size - 1)
            }
        }


        var send_bttn = findViewById<Button>(R.id.send_chat_button)
        var input_field = findViewById<EditText>(R.id.chat_box)
        var schedule_bttn = findViewById<Button>(R.id.schedules_chat_button)
        var grades_bttn = findViewById<Button>(R.id.grades_chat_button)
        var chat_title = findViewById<TextView>(R.id.chat_title)

        chat_title.text = friend_name



        send_bttn.setOnClickListener {
            var in_text:String = input_field.text.toString()

            if (in_text.equals(""))
            {
                return@setOnClickListener
            }
            var san_email: String = Utls.sanitize_str_for_db(MainActivity.user!!.email!!)

            var curr_time: Long = Calendar.getInstance().timeInMillis

            var curr_obj = JSONObject()
            curr_obj.put(ChatMessage.MESSAGE, in_text)
            curr_obj.put(ChatMessage.SENDER, san_email)
            curr_obj.put(ChatMessage.TIME, curr_time)
            input_field.text.clear()

            var curr_msg: ChatMessage = ChatMessage(in_text, san_email, curr_time)
            messages.add(curr_msg)
            messages.sort()

            var json_list: JSONArray = curr_chat.getJSONArray(ChatMessage.CHAT)
            json_list.put(curr_obj)
            curr_chat.put(ChatMessage.CHAT, json_list)
            ChatPersistence.set_chat_json(curr_chat.getString(ChatMessage.IDX), curr_chat, object :
                ICallback {
                override fun callback(data: DataSnapshot?) {
                    println("MESSAGE SENT")
                    rv.scrollToPosition(messages.size - 1)
                }
            })
        }

        schedule_bttn.setOnClickListener {
            // todo
        }

        grades_bttn.setOnClickListener {
            // todo
        }


        ChatPersistence.turn_on_chat_listener(
            curr_chat.get(ChatMessage.IDX).toString(),
            callback
        )
    }


    override fun onBackPressed() {
        println("BACK PRESSED")
        ChatPersistence.turn_off_chat_listener(curr_chat.get(ChatMessage.IDX).toString(), object :
            ICallback {
            override fun callback(data: DataSnapshot?) {
                println("LISTENER DETACHED")
            }
        })
        super.onBackPressed()
    }

    override fun onStop() {
        println("CHAT STOPPED")
        ChatPersistence.turn_off_chat_listener(curr_chat.get(ChatMessage.IDX).toString(), object :
            ICallback {
            override fun callback(data: DataSnapshot?) {
                println("LISTENER DETACHED")
            }
        })
        super.onStop()
    }
}