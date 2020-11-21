package com.example.mobilecomputingproject.chat

import org.json.JSONObject

class ChatMessage(contents: String, sender: String, time: Long, special: Int) : Comparable<ChatMessage> {
    companion object
    {
        const val IDX = "idx"
        const val MESSAGE = "msg"
        const val SENDER = "sender"
        const val TIME = "time"
        const val CHAT = "chat"

        const val SCHEDULE = "schedule"
        const val GRADES = "grades"
        const val SPECIAL = "spec"
    }

    var contents = contents
    var sender = sender
    var time = time
    var grades: JSONObject? = JSONObject()
    var schedule: JSONObject? = JSONObject()
    var special: Int = special


    override fun compareTo(msg: ChatMessage): Int
    {
        var msg_time = msg.time
        var result:Long = time - msg_time
        return if (result < 0)
            -1
        else if (result > 0)
            1
        else
            0
    }

}