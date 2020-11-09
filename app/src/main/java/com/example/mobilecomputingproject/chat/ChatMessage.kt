package com.example.mobilecomputingproject.chat

class ChatMessage(contents: String, sender: String, time: Long) : Comparable<ChatMessage> {
    companion object
    {
        const val IDX = "idx"
        const val MESSAGE = "msg"
        const val SENDER = "sender"
        const val TIME = "time"
        const val CHAT = "chat"
    }

    var contents = contents
    var sender = sender
    var time = time


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