package com.example.mobilecomputingproject.users

import com.example.mobilecomputingproject.MainActivity
import com.example.mobilecomputingproject.helpers.Utls
import com.example.mobilecomputingproject.interfaces.IOnRetrieveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

open class User {
    var email : String? = ""
    var name : String? = ""
    var uid : String? = ""
    var requests : MutableList<String>? = mutableListOf()

    private var curr_ref = MainActivity.fb_db_ref!!.child("Users")


    fun getInitData(email: String, listener : IOnRetrieveData)
    {
        var email_str = Utls.sanitize_str_for_db(email)
        listener.onStart()

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val u = dataSnapshot.child(email_str).getValue<User>()
                if (u == null)
                {
                    listener.onFailed(null)
                    return
                }
                else
                {
                    u.email = email_str
                }

                MainActivity.user!!.email = Utls.sanitize_str_for_db(u.email!!)
                MainActivity.user!!.uid = u.uid
                MainActivity.user!!.name = u.name
                MainActivity.user!!.requests = u.requests

                listener.onSuccess(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        curr_ref.child(email_str).addListenerForSingleValueEvent(postListener)
    }

}