package com.example.mobilecomputingproject.interfaces

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

interface IOnRetrieveData {

    fun onStart()
    fun onSuccess(data : DataSnapshot?)
    fun onFailed(dbError: DatabaseError?)
}