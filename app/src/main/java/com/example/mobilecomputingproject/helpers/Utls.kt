package com.example.mobilecomputingproject.helpers

import com.example.mobilecomputingproject.chat.ChatMessage
import com.example.mobilecomputingproject.MainActivity
import org.json.JSONObject
import java.util.*

class Utls {
    companion object
    {
        fun sanitize_str_for_db(str: String): String {
            var new_str : String = str.replace(".", "✔")
            return new_str
        }

        fun restore_str_from_db(str: String): String
        {
            var new_str : String = str.replace("✔", ".")
            return new_str
        }

        fun get_chat_idx_str(friend1: String, friend2: String): String
        {
            var list = mutableListOf<String>(friend1, friend2)
            list.sort()
            var idx: String = list[0] + list[1]
            return idx
        }

        fun sort_chats_according_to_friends()
        {
            var tmp_list = mutableListOf<JSONObject>()
            var san_email = sanitize_str_for_db(MainActivity.user!!.email!!)
            for(friend in MainActivity.friends)
            {
                var san_friend = sanitize_str_for_db(friend)
                for(json in MainActivity.chats)
                {
                    var comparison_idx:String = get_chat_idx_str(san_email, san_friend)
                    if (json.get(ChatMessage.IDX).equals(comparison_idx))
                    {
                        tmp_list.add(json)
                        break
                    }
                }
            }
            MainActivity.chats = tmp_list
        }

        fun convert_long_time_to_string(time: Long) : String
        {
            var cal: Calendar = Calendar.getInstance()
            cal.timeInMillis = time

            var secs: String  = cal.get(Calendar.SECOND).toString()
            var mins: String  = cal.get(Calendar.MINUTE).toString()
            var hours: String = cal.get(Calendar.HOUR_OF_DAY).toString()

            // clean up strings so they're the same length
            if(hours.length == 1)
                hours = "0"+hours
            if(mins.length == 1)
                mins = "0"+mins
            if(secs.length == 1)
                secs = "0"+secs

            val result_str = hours+":"+mins+":"+secs
            return result_str
        }

    }



}