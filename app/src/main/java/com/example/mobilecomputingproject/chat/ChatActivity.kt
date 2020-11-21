package com.example.mobilecomputingproject.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecomputingproject.MainActivity
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.adapters.ChatTextAdapter
import com.example.mobilecomputingproject.adapters.GradeCalcAdapter
import com.example.mobilecomputingproject.grades.GradeCalcActivity
import com.example.mobilecomputingproject.grades.GradesPersistence
import com.example.mobilecomputingproject.grades.GradingScheme
import com.example.mobilecomputingproject.helpers.Utls
import com.example.mobilecomputingproject.interfaces.ICallback
import com.google.firebase.database.DataSnapshot
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ChatActivity : AppCompatActivity(), ChatTextAdapter.ItemClickListener {

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
        var adapter = ChatTextAdapter(this, messages, this)
        rv.adapter = adapter


        var callback = object : ICallback {
            override fun callback(data: DataSnapshot?) {
                var data_str: String = data?.value.toString()

                var json_obj: JSONObject = JSONObject(data_str)
                curr_chat = json_obj
                var json_list: JSONArray = json_obj.getJSONArray(ChatMessage.CHAT)

                messages.clear()
                for (i in 0 until json_list.length())
                {
                    var curr_obj: JSONObject = json_list.getJSONObject(i)
                    var curr_msg = ChatMessage(curr_obj.getString(ChatMessage.MESSAGE),
                                            curr_obj.getString(ChatMessage.SENDER),
                                            curr_obj.getString(ChatMessage.TIME).toLong(),
                                            curr_obj.getString(ChatMessage.SPECIAL).toInt())
                    if (curr_msg.special == 1)
                    {
                        curr_msg.grades = curr_obj.getJSONObject(ChatMessage.GRADES)
                    }

                    messages.add(curr_msg)
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

            // prevent empty messages from being sent
            if (in_text.equals(""))
            {
                return@setOnClickListener
            }
            var san_email: String = Utls.sanitize_str_for_db(MainActivity.user!!.email!!)

            var curr_time: Long = Calendar.getInstance().timeInMillis
            var special = 0

            var curr_obj = JSONObject()
            curr_obj.put(ChatMessage.MESSAGE, in_text)
            curr_obj.put(ChatMessage.SENDER, san_email)
            curr_obj.put(ChatMessage.TIME, curr_time)
            curr_obj.put(ChatMessage.SPECIAL, special.toString())
            input_field.text.clear()

            var curr_msg: ChatMessage = ChatMessage(in_text, san_email, curr_time, special)
            messages.add(curr_msg)
            messages.sort()

            var json_list: JSONArray = curr_chat.getJSONArray(ChatMessage.CHAT)
            json_list.put(curr_obj)
            curr_chat.put(ChatMessage.CHAT, json_list)
            ChatPersistence.set_chat_json(curr_chat.getString(ChatMessage.IDX), curr_chat, object :
                ICallback {
                override fun callback(data: DataSnapshot?) {
                    rv.scrollToPosition(messages.size - 1)
                }
            })
        }

        schedule_bttn.setOnClickListener {
            // todo
        }

        grades_bttn.setOnClickListener {
            val intent = Intent(applicationContext, ChatGradingSchemeSelector::class.java)
            startActivity(intent)
        }


        ChatPersistence.turn_on_chat_listener(
            curr_chat.get(ChatMessage.IDX).toString(),
            callback
        )
    }


    override fun onBackPressed() {
        ChatPersistence.turn_off_chat_listener(curr_chat.get(ChatMessage.IDX).toString(), object :
            ICallback {
            override fun callback(data: DataSnapshot?) {
                println("LISTENER DETACHED")
            }
        })
        super.onBackPressed()
    }

    override fun onItemClick(position: Int) {
        // If this message contains a grading scheme then receive it into our grading schemes
        if (messages[position].special == 1)
        {

            var json = JSONObject(messages[position].grades.toString())
            val new_key = GradesPersistence.getNewSchemeKey()
            if (new_key != null) {
                json.put(GradingScheme.UID, new_key)
                GradesPersistence.set_scheme_json(new_key, json, object : ICallback {
                    override fun callback(data: DataSnapshot?) {
                        Toast.makeText(applicationContext, "Received Grading Scheme!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

}