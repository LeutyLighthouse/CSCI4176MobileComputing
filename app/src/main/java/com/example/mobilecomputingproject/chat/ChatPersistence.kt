package com.example.mobilecomputingproject.chat

import com.example.mobilecomputingproject.MainActivity
import com.example.mobilecomputingproject.helpers.Utls
import com.example.mobilecomputingproject.interfaces.ICallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import org.json.JSONArray
import org.json.JSONObject

class ChatPersistence {

    companion object
    {
        val curr_ref = MainActivity.fb_db_ref!!.child("Chats")
        private lateinit var state_listener: ValueEventListener

        fun initChats(email: String, friends: MutableList<String>, cb: ICallback)
        {
            var email_str = email
            email_str = Utls.sanitize_str_for_db(email_str)

            for (friend in friends)
            {
                var friend_san = Utls.sanitize_str_for_db(friend)
                var curr_idx = Utls.get_chat_idx_str(email_str, friend_san)

                curr_ref.child(curr_idx).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var chat_json: JSONArray = JSONArray()
                        var chat_obj: JSONObject = JSONObject()
                        chat_obj.put(ChatMessage.IDX, curr_idx)
                        chat_obj.put(ChatMessage.CHAT, chat_json)
                        var obj_str = chat_obj.toString()

                        if (!dataSnapshot.exists()) {
                            curr_ref.child(curr_idx).setValue(obj_str)
                        }

                        Utls.sort_chats_according_to_friends()

                        // let the app know we're done getting the info
                        cb.callback(null)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })

            }
        }

        fun getChatsHelper(email:String, friends:MutableList<String>, cb: ICallback)
        {
            var email_str = email
            email_str = Utls.sanitize_str_for_db(email_str)

            curr_ref.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    MainActivity.chats.clear()
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            var answer: String? = snapshot.key
                            for (friend in friends) {
                                var friend_san = Utls.sanitize_str_for_db(friend)
                                var curr_idx = Utls.get_chat_idx_str(email_str, friend_san)

                                if (answer != null && answer.equals(curr_idx)) {
                                    var json_str: String? = snapshot.getValue<String>()
                                    var curr_json = JSONObject(json_str)
                                    MainActivity.chats.add(curr_json)
                                    break
                                }
                            }
                        }
                    }
                    Utls.sort_chats_according_to_friends()
                    cb.callback(null)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }



        fun turn_on_chat_listener(chat_idx: String, cb: ICallback)
        {
            var chat_idx_san:String = Utls.sanitize_str_for_db(chat_idx)

            state_listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        cb.callback(dataSnapshot)
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }

            curr_ref.child(chat_idx_san).addValueEventListener(state_listener)
        }

        fun turn_off_chat_listener(chat_idx: String, cb: ICallback)
        {
            var chat_idx_san:String = Utls.sanitize_str_for_db(chat_idx)
            curr_ref.child(chat_idx_san).removeEventListener(state_listener)
            cb.callback(null)
        }

        fun set_chat_json(chat_idx: String, json: JSONObject, cb: ICallback)
        {
            var chat_idx_san: String = Utls.sanitize_str_for_db(chat_idx)

            curr_ref.child(chat_idx_san).setValue(json.toString())
            cb.callback(null)
        }
    }

}