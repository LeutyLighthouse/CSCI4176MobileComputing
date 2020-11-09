package com.example.mobilecomputingproject


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.mobilecomputingproject.chat.ChatPersistence
import com.example.mobilecomputingproject.chat.ChatSelectorActivity
import com.example.mobilecomputingproject.helpers.Utls
import com.example.mobilecomputingproject.interfaces.ICallback
import com.example.mobilecomputingproject.interfaces.IOnRetrieveData
import com.example.mobilecomputingproject.users.FriendManagerActivity
import com.example.mobilecomputingproject.users.User
import com.example.mobilecomputingproject.users.UserPersistence

import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 123
        var fb_user : FirebaseUser? = null //= Firebase.auth.currentUser
        var user : User? = null
        var fb_db_ref: DatabaseReference? = null //= Firebase.database.reference

        var requests = mutableListOf<String>()
        var friends = mutableListOf<String>()
        var chats = mutableListOf<JSONObject>()
    }


    object listener : IOnRetrieveData
    {
        override fun onStart() {}
        override fun onSuccess(data: DataSnapshot?) {
        }
        override fun onFailed(dbError: DatabaseError?) {}

    }

    fun showFirebaseUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val main_intent = Intent(this, this::class.java)
                startActivity(main_intent)

            } else {
                if(response == null)
                {
                    println("User backed out of sign in process");
                }
            }
        }
    }

    private fun getFriends()
    {
        if (user != null) {
            UserPersistence.getFriendsHelper(MainActivity.user!!.email!!, object : ICallback {
                override fun callback(data: DataSnapshot?) {
                    initChats()
                }
            })
        }
    }

    private fun initChats()
    {
        if (user != null) {
            ChatPersistence.initChats(MainActivity.user!!.email!!, friends, object : ICallback {
                override fun callback(data: DataSnapshot?) {
                    getChats()
                }
            })
        }
    }

    private fun getChats()
    {
        if (user != null) {
            ChatPersistence.getChatsHelper(MainActivity.user!!.email!!, friends, object :
                ICallback {
                override fun callback(data: DataSnapshot?) {
                    Utls.sort_chats_according_to_friends()
                }
            })
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        if (fb_db_ref == null)
        {
            var db: FirebaseDatabase = FirebaseDatabase.getInstance()
            db.setPersistenceEnabled(true)
            fb_db_ref = db.reference
        }



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        fb_user = Firebase.auth.currentUser
        fb_db_ref = Firebase.database.reference

        if (fb_user == null)
        {
            showFirebaseUI()
        }
        else
        {
            if(user == null)
            {
                user = User()
                user?.name = fb_user?.displayName
                user?.email = Utls.sanitize_str_for_db(fb_user?.email!!)
                user?.uid = fb_user?.uid
                user?.getInitData(fb_user?.email!!, listener)
            }
            addLoginInfoToDB()
            Toast.makeText(applicationContext,"Signed in!", Toast.LENGTH_SHORT).show()
        }

        val title = findViewById<TextView>(R.id.main_title).also {
            if(fb_user != null)
                it.setText("Hello "+ fb_user!!.displayName!!+"!\nWelcome to Courses!")
        }

        getFriends()

        val signout_bttn = findViewById<Button>(R.id.sign_out_bttn)
        signout_bttn.setOnClickListener {


            Firebase.auth.signOut()
            user = null
            fb_user = null

            val main_intent = Intent(this, this::class.java)
            startActivity(main_intent)

        }

        val friend_manager_button = findViewById<Button>(R.id.fr_button)
        friend_manager_button.setOnClickListener {
            val fr_intent = Intent(this, FriendManagerActivity::class.java)
            startActivity(fr_intent)

        }

        val delete_bttn = findViewById<Button>(R.id.delete_button)
        delete_bttn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure that you want to delete your profile?\nIt cannot be recovered after deleting.")
                .setPositiveButton(android.R.string.yes, object: DialogInterface.OnClickListener
                {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        println("DELETE SELECTED")
                    }
                })
                .setNeutralButton("No", null)
                .setIcon(R.drawable.ic_baseline_delete_forever_24)
                .show()
        }

        val chat_bttn = findViewById<Button>(R.id.chat_button)
        chat_bttn.setOnClickListener {
            UserPersistence.getFriendsHelper(MainActivity.user!!.email!!,
                object: ICallback {
                override fun callback(data: DataSnapshot?) {
                    ChatPersistence.getChatsHelper(MainActivity.user!!.email!!, MainActivity.friends,
                        object: ICallback {
                        override fun callback(data: DataSnapshot?) {
                            val intent = Intent(applicationContext, ChatSelectorActivity::class.java)
                            startActivity(intent)
                        }
                    });


                }
            })

        }


    }

    override fun onBackPressed() {
        println("BACK PRESSED IN MAIN")
        return
    }

    fun addLoginInfoToDB()
    {
        val san_email = Utls.sanitize_str_for_db(fb_user!!.email!!)
        val curr_ref = fb_db_ref?.child("Users")?.child(san_email)
        curr_ref!!.child("name").setValue(fb_user!!.displayName)
        curr_ref!!.child("uid").setValue(fb_user!!.uid)
    }
}