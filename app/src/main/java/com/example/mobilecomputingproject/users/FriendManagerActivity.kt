package com.example.mobilecomputingproject.users

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilecomputingproject.MainActivity
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.chat.ChatPersistence
import com.example.mobilecomputingproject.helpers.Utls
import com.example.mobilecomputingproject.interfaces.ICallback
import com.google.firebase.database.DataSnapshot


class FriendManagerActivity : AppCompatActivity() {
    companion object
    {
        val empty_notifier = "None available."
    }

    lateinit var adapter: ArrayAdapter<String>
    lateinit var remove_adapter: ArrayAdapter<String>
    lateinit var accept_reject_spinner: Spinner
    lateinit var remove_spinner: Spinner
    var accept_reject_selected_email: String = ""
    var remove_selected_email: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_manager)

        accept_reject_spinner = findViewById<Spinner>(R.id.fr_spinner)
        remove_spinner = findViewById<Spinner>(R.id.fr_remove_spinner)
        val accept_bttn = findViewById<Button>(R.id.fr_accept_button)
        val reject_bttn = findViewById<Button>(R.id.fr_reject_button)
        val fr_edit_text = findViewById<EditText>(R.id.fr_send_edittext)
        val send_bttn = findViewById<Button>(R.id.fr_send_button)
        val remove_bttn = findViewById<Button>(R.id.fr_remove_button)


        MainActivity.friends.clear()
        MainActivity.requests.clear()
        MainActivity.friends.add(empty_notifier)
        MainActivity.requests.add(empty_notifier)
        getRequests()
        getFriends()

        accept_bttn.setOnClickListener {
            if (!accept_reject_spinner.selectedItem.toString().equals(Utls.restore_str_from_db(
                    empty_notifier
                )))
            {
                acceptSelected(accept_reject_spinner.selectedItem.toString())
            }
        }


        reject_bttn.setOnClickListener {
            if (!accept_reject_spinner.selectedItem.toString().equals(Utls.restore_str_from_db(
                    empty_notifier
                )))
            {
                rejectSelected(accept_reject_spinner.selectedItem.toString())
            }
        }


        send_bttn.setOnClickListener {
            var email_str = fr_edit_text.text.toString()

            if(!email_str.equals(Utls.restore_str_from_db(MainActivity.user!!.email!!))
                && !email_str.equals(""))
                sendRequest(email_str)
            fr_edit_text.text.clear()
        }


        remove_bttn.setOnClickListener {
            if (!remove_spinner.selectedItem.toString().equals(Utls.restore_str_from_db(
                    empty_notifier
                )))
            {
                removeSelected(remove_spinner.selectedItem.toString())
            }
        }


    }

    fun updateChats()
    {
        ChatPersistence.initChats(MainActivity.user!!.email!!, MainActivity.friends, object:
            ICallback {
            override fun callback(data: DataSnapshot?) {
            }
        });
    }

    fun getRequests()
    {
        UserPersistence.getRequestsHelper(MainActivity.user!!.email!!, object : ICallback {
            override fun callback(data: DataSnapshot?) {

                adapter = ArrayAdapter<String>(
                    applicationContext,
                    android.R.layout.simple_spinner_item,
                    MainActivity.requests
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                accept_reject_spinner.adapter = adapter
                accept_reject_spinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (parent != null)
                                accept_reject_selected_email = Utls.sanitize_str_for_db(
                                    parent?.getItemAtPosition(position).toString()
                                )
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                adapter.notifyDataSetChanged()
            }
        })
    }

    fun getFriends()
    {
        UserPersistence.getFriendsHelper(MainActivity.user!!.email!!, object : ICallback {
            override fun callback(data: DataSnapshot?) {
                remove_adapter = ArrayAdapter<String>(
                    applicationContext,
                    android.R.layout.simple_spinner_item,
                    MainActivity.friends
                )
                remove_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                remove_spinner.adapter = remove_adapter
                remove_spinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            remove_selected_email = Utls.sanitize_str_for_db(
                                parent?.getItemAtPosition(position).toString()
                            )
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                remove_adapter.notifyDataSetChanged()
            }
        })
    }

    fun removeSelected(email: String)
    {
        UserPersistence.removeSelectedHelper(email, object : ICallback {
            override fun callback(data: DataSnapshot?) {
                getRequests()
                getFriends()
                updateChats()
            }
        })
    }

    fun rejectSelected(email: String)
    {
        UserPersistence.rejectSelectedHelper(email, object : ICallback {
            override fun callback(data: DataSnapshot?) {
                getRequests()
                getFriends()
            }
        })
    }


    fun acceptSelected(email: String)
    {
        UserPersistence.acceptSelectedHelper(email, object : ICallback {
            override fun callback(data: DataSnapshot?) {
                getRequests()
                getFriends()
                updateChats()
            }
        })
    }

    fun sendRequest(email: String)
    {
        UserPersistence.sendRequestHelper(email, object : ICallback {
            override fun callback(data: DataSnapshot?) {
            }
        })
    }

}