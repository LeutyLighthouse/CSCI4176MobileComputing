package com.example.mobilecomputingproject.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecomputingproject.MainActivity
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.adapters.ChatGradesAdapter
import com.example.mobilecomputingproject.adapters.GradeCalcAdapter
import com.example.mobilecomputingproject.adapters.GradeCalcSelectorAdapter
import com.example.mobilecomputingproject.grades.GradeCalcActivity
import com.example.mobilecomputingproject.grades.GradeCalculatorSelector
import com.example.mobilecomputingproject.grades.GradesPersistence
import com.example.mobilecomputingproject.grades.GradingScheme
import com.example.mobilecomputingproject.helpers.Utls
import com.example.mobilecomputingproject.interfaces.ICallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.getValue
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ChatGradingSchemeSelector : AppCompatActivity(), ChatGradesAdapter.ItemClickListener  {

    var grading_scheme_names = mutableListOf<String>()
    var grading_schemes = mutableListOf<JSONObject>()

    lateinit var adapter: ChatGradesAdapter
    lateinit var recyclerView: RecyclerView

    var callback = object : ICallback {
        override fun callback(data: DataSnapshot?) {

            if (data != null) {
                grading_schemes.clear()
                grading_scheme_names.clear()

                for (snapshot in data.children) {
                    var answer: String? = snapshot.getValue<String>()
                    var curr_json: JSONObject = JSONObject(answer)
                    grading_schemes.add(curr_json)
                }

                for (json in grading_schemes)
                {
                    grading_scheme_names.add(json.get(GradingScheme.TITLE).toString())
                }
            }
            adapter.notifyDataSetChanged()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_grading_scheme_selector)

        recyclerView = findViewById<RecyclerView>(R.id.grade_selector_list)
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        var itemDeco =  DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(itemDeco)

        adapter = ChatGradesAdapter(this, grading_scheme_names, this)
        recyclerView.adapter = adapter

        GradesPersistence.turn_on_schemes_listener(callback)
    }


    override fun onItemClick(position: Int) {
        var in_text:String = "[grading scheme, title: ${grading_scheme_names[position]}]\nClick to add."
        var san_email: String = Utls.sanitize_str_for_db(MainActivity.user!!.email!!)
        var curr_time: Long = Calendar.getInstance().timeInMillis
        var special = 1

        var curr_obj = JSONObject()
        curr_obj.put(ChatMessage.MESSAGE, in_text)
        curr_obj.put(ChatMessage.SENDER, san_email)
        curr_obj.put(ChatMessage.TIME, curr_time)
        curr_obj.put(ChatMessage.SPECIAL, special.toString())
        curr_obj.put(ChatMessage.GRADES, grading_schemes[position])

        var curr_msg: ChatMessage = ChatMessage(in_text, san_email, curr_time, special)
        ChatActivity.messages.add(curr_msg)
        ChatActivity.messages.sort()

        var json_list: JSONArray = ChatActivity.curr_chat.getJSONArray(ChatMessage.CHAT)
        json_list.put(curr_obj)
        ChatActivity.curr_chat.put(ChatMessage.CHAT, json_list)
        ChatPersistence.set_chat_json(
            ChatActivity.curr_chat.getString(ChatMessage.IDX),
            ChatActivity.curr_chat, object :
            ICallback {
            override fun callback(data: DataSnapshot?) {
                onBackPressed()
            }
        })
    }


    override fun onBackPressed() {
        GradesPersistence.turn_off_schemes_listener(object: ICallback
        {
            override fun callback(data: DataSnapshot?) {
            }
        })
        super.onBackPressed()
    }

    override fun onStop() {
        GradesPersistence.turn_off_schemes_listener(object: ICallback
        {
            override fun callback(data: DataSnapshot?) {
            }
        })
        super.onStop()
    }
}