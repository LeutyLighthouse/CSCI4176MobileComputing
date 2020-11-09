package com.example.mobilecomputingproject.users

import com.example.mobilecomputingproject.MainActivity
import com.example.mobilecomputingproject.helpers.Utls
import com.example.mobilecomputingproject.interfaces.ICallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class UserPersistence {

    companion object
    {
        val curr_ref = MainActivity.fb_db_ref!!.child("Users")

        fun getFriends()
        {
            getFriendsHelper(MainActivity.user!!.email!!, object: ICallback {
                override fun callback(data: DataSnapshot?) {
                    // do nothing
                }
            })
        }


        fun getRequestsHelper(email: String, cb: ICallback) {
            var email_str = email
            email_str = Utls.sanitize_str_for_db(email_str)

            curr_ref.child(email_str).child("friend requests").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    MainActivity.requests.clear()
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            var answer: String? = snapshot.getValue<String>()
                            if (answer != null)
                                MainActivity.requests.add(Utls.restore_str_from_db(answer))
                        }
                    }

                    if (MainActivity.requests.size == 0)
                        MainActivity.requests.add(FriendManagerActivity.empty_notifier)
                    // let the app know we're done getting the info
                    cb.callback(null)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

        fun getFriendsHelper(email: String, cb: ICallback) {
            var email_str = email
            email_str = Utls.sanitize_str_for_db(email_str)


            curr_ref.child(email_str).child("friends").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    MainActivity.friends.clear()
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            var answer: String? = snapshot.getValue<String>()
                            if (answer != null)
                                MainActivity.friends.add(Utls.restore_str_from_db(answer))
                        }
                    }

                    if (MainActivity.friends.size == 0)
                        MainActivity.friends.add(FriendManagerActivity.empty_notifier)
                    // let the app know we're done getting the info
                    cb.callback(null)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

        fun removeSelectedHelper(email: String, cb: ICallback) {
            var email_str = email
            email_str = Utls.sanitize_str_for_db(email_str)

            // remove from my friends
            curr_ref.child(MainActivity.user!!.email!!).child("friends").addListenerForSingleValueEvent(
                object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.children) {
                                var answer: String? = snapshot.getValue<String>()
                                if (answer != null && answer.equals(email_str)) {
                                    // found the old friend
                                    snapshot.ref.removeValue()
                                }
                            }
                            cb.callback(null)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })

            // remove from their friends
            curr_ref.child(email_str).child("friends").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            var answer: String? = snapshot.getValue<String>()
                            if (answer != null && answer.equals(MainActivity.user!!.email!!)) {
                                // found the old friend
                                snapshot.ref.removeValue()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }


        fun rejectSelectedHelper(email: String, cb: ICallback) {
            var email_str = email
            email_str = Utls.sanitize_str_for_db(email_str)

            // remove from my requests
            curr_ref.child(MainActivity.user!!.email!!).child("friend requests").addListenerForSingleValueEvent(
                object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.children) {
                                var answer: String? = snapshot.getValue<String>()
                                if (answer != null && answer.equals(email_str)) {
                                    // found the old request
                                    snapshot.ref.removeValue()
                                }
                            }
                            cb.callback(null)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })

            // remove from their requests
            curr_ref.child(email_str).child("friend requests").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            var answer: String? = snapshot.getValue<String>()
                            if (answer != null && answer.equals(MainActivity.user!!.email!!)) {
                                // found the old request
                                snapshot.ref.removeValue()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

        fun acceptSelectedHelper(email: String, cb: ICallback) {
            var email_str = email
            email_str = Utls.sanitize_str_for_db(email_str)

            // add to my friends
            curr_ref.child(MainActivity.user!!.email!!).child("friends").addListenerForSingleValueEvent(
                object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var found = false
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.children) {
                                var answer: String? = snapshot.getValue<String>()
                                if (answer != null && answer.equals(email_str)) {
                                    found = true
                                }
                            }
                        }
                        if (!found) {
                            val new_idx =
                                curr_ref.child(MainActivity.user!!.email!!).child("friends").push()
                            new_idx.setValue(email_str)
                        }
                        cb.callback(null)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })

            // add to their friends
            curr_ref.child(email_str).child("friends").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var found = false
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            var answer: String? = snapshot.getValue<String>()
                            if (answer != null && answer.equals(MainActivity.user!!.email!!)) {
                                found = true
                            }
                        }
                    }
                    if (!found) {
                        val new_idx = curr_ref.child(email_str).child("friends").push()
                        new_idx.setValue(MainActivity.user!!.email!!)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

            // remove from my requests
            curr_ref.child(MainActivity.user!!.email!!).child("friend requests").addListenerForSingleValueEvent(
                object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.children) {
                                var answer: String? = snapshot.getValue<String>()
                                if (answer != null && answer.equals(email_str)) {
                                    // found the old request
                                    snapshot.ref.removeValue()
                                }
                            }
                        }
                        cb.callback(null)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })

            // remove from their requests
            curr_ref.child(email_str).child("friend requests").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            var answer: String? = snapshot.getValue<String>()
                            if (answer != null && answer.equals(MainActivity.user!!.email!!)) {
                                // found the old request
                                snapshot.ref.removeValue()
                            }
                        }
                    }
                    cb.callback(null)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

        fun sendRequestHelper(email: String, cb: ICallback)
        {
            var email_str = email
            email_str = Utls.sanitize_str_for_db(email_str)

            curr_ref.child(email_str).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var dont_send = false
                    if (dataSnapshot.exists()) {
                        //check to see if they're already friends
                        var friends_snap = dataSnapshot.child("friends")
                        for (snapshot in friends_snap.children) {
                            var answer: String? = snapshot.getValue<String>()
                            if (answer != null && answer.equals(MainActivity.user!!.email)) {
                                dont_send = true
                            }
                        }

                        //check to see if a request is already sent
                        var friend_requests_snap = dataSnapshot.child("friend requests")
                        for (snapshot in friend_requests_snap.children) {
                            var answer: String? = snapshot.getValue<String>()
                            if (answer != null && answer.equals(MainActivity.user!!.email)) {
                                dont_send = true
                            }
                        }
                    }
                    else if (!dataSnapshot.exists()){
                        // entry doesn't exist, don't write
                        dont_send = true;
                    }
                    if (!dont_send) {
                        val new_idx = curr_ref.child(email_str).child("friend requests").push()
                        new_idx.setValue(MainActivity.user!!.email)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

    }
}